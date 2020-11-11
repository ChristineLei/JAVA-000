package com.main;

import java.util.concurrent.Semaphore;

public class SemaphoreDemo {
    public static int threadCount = 2;
    public static void main(String[] args) throws InterruptedException {

        long start=System.currentTimeMillis();
        // 在这里创建一个线程或线程池，
        final Semaphore semaphore = new Semaphore(1);
        new Runnable(){
            public void run(){
                try {
                    semaphore.acquire();
                    int result = sum();
                    semaphore.release();
                    System.out.println("异步计算结果为："+result);
                    System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.run();
        semaphore.release();
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
