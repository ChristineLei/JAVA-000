package io.java0.clei.service;

import io.lettuce.core.api.StatefulRedisConnection;
import io.java0.clei.RedisDistributedLock;

public interface InventoryService {
    boolean decrease(RedisDistributedLock redisDistributedLock, StatefulRedisConnection<String, String> connection);
}