package io.java0.clei.service.impl;

import io.lettuce.core.api.StatefulRedisConnection;
import io.java0.clei.service.InventoryService;
import io.java0.clei.RedisDistributedLock;

import java.util.UUID;

public class InventoryServiceImpl implements InventoryService {
    private Long n = 1000L;
    @Override
    public boolean decrease(RedisDistributedLock redisDistributedLock, StatefulRedisConnection<String, String> connection) {
        String value = UUID.randomUUID().toString();
        if(redisDistributedLock.lockWithTimeout(connection,value,1000L)){
            n--;
            System.out.println(Thread.currentThread().getName() + "--减库存到:::" + n);
            redisDistributedLock.releaseLockWithLua(connection,value);
            return true;
        }
        System.out.println(Thread.currentThread().getName() + "---没有执行减少库存操作");
        return false;
    }
}

