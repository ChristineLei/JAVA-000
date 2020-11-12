package com.main;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        long start=System.currentTimeMillis();
        // 在这里创建一个线程或线程池，
        new Runnable(){
            public void run(){
                synchronized (this){
                    int result = sum();
                    System.out.println("异步计算结果为："+result);
                    System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
                    countDownLatch.countDown();
                }
            }
        }.run();
        countDownLatch.await();

    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}
