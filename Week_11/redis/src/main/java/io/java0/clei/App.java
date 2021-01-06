package io.java0.clei;

import io.lettuce.core.api.StatefulRedisConnection;
import io.java0.clei.service.InventoryService;
import io.java0.clei.service.impl.InventoryServiceImpl;

import java.util.concurrent.CountDownLatch;

public class App  {
    private final static int threadCount = 200;
    public static void main( String[] args ) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        InventoryService inventoryService = new InventoryServiceImpl();
        RedisDistributedLock redisDistributedLock = new RedisDistributedLock();
        StatefulRedisConnection<String, String> connection = redisDistributedLock.createConnection();
        for (int i = 0; i< threadCount; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    inventoryService.decrease(redisDistributedLock,connection);
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
        redisDistributedLock.shutdownConnection();
    }


}
