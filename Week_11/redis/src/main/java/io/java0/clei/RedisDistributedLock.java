package io.java0.clei;

import io.lettuce.core.RedisClient;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisScriptingCommands;
import io.lettuce.core.api.sync.RedisStringCommands;


public class RedisDistributedLock {
    private final static String REDIS_URL = "redis://120.79.218.62:6379";

    private final static String LOCK_KEY = "lock";

    private RedisClient redisClient;

    public RedisClient getRedisClient() {
        return redisClient;
    }


    public RedisDistributedLock() {
        this.redisClient = RedisClient.create(REDIS_URL);
    }

    public StatefulRedisConnection<String, String> createConnection(){
        StatefulRedisConnection<String, String> connection =  this.redisClient.connect();
        return connection;
    }

    public  Boolean lockWithTimeout(StatefulRedisConnection<String, String> connection, String value,Long timeout){
        RedisStringCommands sync =  connection.sync();
        try {
            synchronized(this){
                if (sync.set(LOCK_KEY, value, new SetArgs().nx().px(timeout)) != null){
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        RedisDistributedLock redisDistributedLock = new RedisDistributedLock();
        StatefulRedisConnection<String, String> createConnection = redisDistributedLock.createConnection();
        System.out.println(redisDistributedLock.lockWithTimeout(createConnection, "1212", 500L));
        Thread.sleep(50);
        System.out.println(redisDistributedLock.lockWithTimeout(createConnection, "12232312", 500L));
        redisDistributedLock.shutdownConnection();
    }

    public  Boolean releaseLockWithLua(StatefulRedisConnection<String, String> connection, String value) {
        String luaScript = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                "return redis.call(\"del\",KEYS[1]) else\n" +
                "return 0 end";
        try {
            RedisScriptingCommands sync =  connection.sync();
            String[] keys = new String[]{LOCK_KEY};
            String[] args = new String[]{value};
            return (Boolean) sync.eval(luaScript, ScriptOutputType.BOOLEAN,keys,args);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void shutdownConnection(){
        this.redisClient.shutdown();
    }
}

