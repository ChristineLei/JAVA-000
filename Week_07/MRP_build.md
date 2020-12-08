# Setting up MySQL Group Replication with MySQL Docker images

## References
- https://mysqlhighavailability.com/setting-up-mysql-group-replication-with-mysql-docker-images/

## 1. Overview

We start by downloading the MySQL 8 image from Docker Hub (hub.docker.com/r/mysql/mysql-server/), 
then we are going to create a Docker network named groupnet and finally setup a Multi-Primary GR topology with 3 group members in different containers. 

## 2. Pull MySQL Sever Image

To download the *MySQL Community Edition image*, the command is:
```
docker pull mysql/mysql-server:tag
```
If `:tag` is omitted, the latest tag is used, and the image for the latest GA version of MySQL Server is downloaded.

Examples:
```
docker pull mysql/mysql-server
docker pull mysql/mysql-server:5.7
docker pull mysql/mysql-server:8.0
```
In this example, we are going to use ***mysql/mysql-server:5.7***

## 3. Creating a Docker network
Fire the following command to create a network:
```
$ docker network create groupnet
```
You just need to create it once, unless you remove it from Docker.

To see all Docker networks:
```
$ docker network ls
```
## 4. Creating 3 MySQL containers

Run the commands below in a terminal.
for multi-primary mode:
```
$ for N in 1 2 3
> do docker run -d --name=node$N --net=groupnet --hostname=node$N \
>   -v $PWD/d$N:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=mypass \
>   mysql/mysql-server:5.7 \
>   --server-id=$N \
>   --log-bin='mysql-bin-1.log' \
>   --enforce-gtid-consistency='ON' \
>   --log-slave-updates='ON' \
>   --gtid-mode='ON' \
>   --transaction-write-set-extraction='XXHASH64' \
>   --binlog-checksum='NONE' \
>   --master-info-repository='TABLE' \
>   --relay-log-info-repository='TABLE' \
>   --plugin-load='group_replication.so' \
>   --relay-log-recovery='ON' \
>   --group-replication-start-on-boot='OFF' \
>   --group-replication-group-name='aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee' \
>   --group-replication-local-address="node$N:33061" \
>   --group-replication-group-seeds='node1:33061,node2:33061,node3:33061' \
>   --loose-group-replication-single-primary-mode='OFF' \
>   --loose-group-replication-enforce-update-everywhere-checks='ON'
> done
```
for single primary mode:
change the loose-group-replication-single-primary-mode and loose-group-replication-enforce-update-everywhere-checks values to ‘ON’ and ‘OFF’ respectively
-v $PWD/d$N:/var/lib/mysql: It sets up a mount volume that links the /var/lib/mysql directory from inside the node container to the  $PWD/d$N  directory on the host machine.

It's possible to see whether the containers are started by running:
```
$ docker ps -a
```
```console
CONTAINER ID        IMAGE                    COMMAND                  CREATED             STATUS                            PORTS                 NAMES
b871fc7d745c        mysql/mysql-server:5.7   "/entrypoint.sh --se…"   6 seconds ago       Up 2 seconds (health: starting)   3306/tcp, 33060/tcp   node3
34e18f70fb3c        mysql/mysql-server:5.7   "/entrypoint.sh --se…"   8 seconds ago       Up 6 seconds (health: starting)   3306/tcp, 33060/tcp   node2
c0fab535bac2        mysql/mysql-server:5.7   "/entrypoint.sh --se…"   9 seconds ago       Up 8 seconds (health: starting)   3306/tcp, 33060/tcp   node1
```
Servers are still with status **(health: starting)**, wait till they are with state **(healthy)** before running the following commands.
```console
CONTAINER ID        IMAGE                    COMMAND                  CREATED             STATUS                   PORTS                 NAMES
b871fc7d745c        mysql/mysql-server:5.7   "/entrypoint.sh --se…"   6 minutes ago       Up 6 minutes (healthy)   3306/tcp, 33060/tcp   node3
34e18f70fb3c        mysql/mysql-server:5.7   "/entrypoint.sh --se…"   6 minutes ago       Up 6 minutes (healthy)   3306/tcp, 33060/tcp   node2
c0fab535bac2        mysql/mysql-server:5.7   "/entrypoint.sh --se…"   7 minutes ago       Up 7 minutes (healthy)   3306/tcp, 33060/tcp   node1

if some problem happends,  check the Mysql Logs: $ docker logs node1

## 5. Setting up and starting GR in the containers
### 5.1. node1

Let's configure the **master node**.

```
$ docker exec -it node1 mysql -uroot -pmypass \
>   -e "SET @@GLOBAL.group_replication_bootstrap_group=1;" \
>   -e "create user 'repl'@'%';" \
>   -e "GRANT REPLICATION SLAVE ON *.* TO repl@'%';" \
>   -e "flush privileges;" \
>   -e "change master to master_user='repl' for channel 'group_replication_recovery';" \
>   -e "START GROUP_REPLICATION;" \
>   -e "SET @@GLOBAL.group_replication_bootstrap_group=0;" \
>   -e "SELECT * FROM performance_schema.replication_group_members;"
```
```console
mysql: [Warning] Using a password on the command line interface can be insecure.
+---------------------------+--------------------------------------+-------------+-------------+--------------+
| CHANNEL_NAME              | MEMBER_ID                            | MEMBER_HOST | MEMBER_PORT | MEMBER_STATE |
+---------------------------+--------------------------------------+-------------+-------------+--------------+
| group_replication_applier | e25a081a-33ab-11eb-b0d1-0242ac130002 | node1       |        3306 | ONLINE       |
+---------------------------+--------------------------------------+-------------+-------------+--------------+
```
### 5.2. node2, node3
Use the Performance Schema tables to monitor GR:

```
$ for N in 2 3
> do docker exec -it node$N mysql -uroot -pmypass \
>   -e "change master to master_user='repl' for channel 'group_replication_recovery';" \
>   -e "START GROUP_REPLICATION;"
> done

$ docker exec -it node1 mysql -uroot -pmypass \
>   -e "SELECT * FROM performance_schema.replication_group_members;"
```

```console
mysql: [Warning] Using a password on the command line interface can be insecure.
+---------------------------+--------------------------------------+-------------+-------------+--------------+
| CHANNEL_NAME              | MEMBER_ID                            | MEMBER_HOST | MEMBER_PORT | MEMBER_STATE |
+---------------------------+--------------------------------------+-------------+-------------+--------------+
| group_replication_applier | e25a081a-33ab-11eb-b0d1-0242ac130002 | node1       |        3306 | ONLINE       |
| group_replication_applier | e49fd907-33ab-11eb-b15f-0242ac130003 | node2       |        3306 | ONLINE       |
| group_replication_applier | e80a7a00-33ab-11eb-b260-0242ac130004 | node3       |        3306 | ONLINE       |
+---------------------------+--------------------------------------+-------------+-------------+--------------+
```

## 6. Inserting some data

We are going to create a new database named "TEST" in node1.
```
$ docker exec -it node1 mysql -uroot -pmypass \
>   -e "create database TEST; use TEST; CREATE TABLE t1 (id INT NOT NULL PRIMARY KEY) ENGINE=InnoDB; show tables;"
```
Output:
```console
mysql: [Warning] Using a password on the command line interface can be insecure.
+----------------+
| Tables_in_test |
+----------------+
| t1             |
+----------------+
```
Let’s add some data by connecting to the other group members:
```
for N in 2 3
do docker exec -it node$N mysql -uroot -pmypass \
  -e "INSERT INTO TEST.t1 VALUES($N);"
done
```

Let’s see whether the data was inserted:
```
$ for N in 1 2 3
> do docker exec -it node$N mysql -uroot -pmypass \
>   -e "SHOW VARIABLES WHERE Variable_name = 'hostname';" \
>   -e "SELECT * FROM TEST.t1;"
> done
```
Output:
```console
mysql: [Warning] Using a password on the command line interface can be insecure.
+---------------+-------+
| Variable_name | Value |
+---------------+-------+
| hostname      | node1 |
+---------------+-------+
+----+
| id |
+----+
|  2 |
|  3 |
+----+
mysql: [Warning] Using a password on the command line interface can be insecure.
+---------------+-------+
| Variable_name | Value |
+---------------+-------+
| hostname      | node2 |
+---------------+-------+
+----+
| id |
+----+
|  2 |
|  3 |
+----+
mysql: [Warning] Using a password on the command line interface can be insecure.
+---------------+-------+
| Variable_name | Value |
+---------------+-------+
| hostname      | node3 |
+---------------+-------+
+----+
| id |
+----+
|  2 |
|  3 |
+----+
```

## 7. Cleaning up: stopping containers, removing created network and image

#### To stop the running containers:
```
$ docker stop node1 node2 node3
docker rm node1 node2 node3
```
#### To remove the data directories created (they are located in the folder were the containers were run):
```
$ sudo rm -rf d1 d2 d3
```
#### To remove the created network:
```
$ docker network rm groupnet
```
#### To remove MySQL image:
```
$ docker rmi mysql/mysql-server:5.7
```
