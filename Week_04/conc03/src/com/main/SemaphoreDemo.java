package com.main;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Semaphore;

public class SemaphoreDemo {
    private final static int threadCount = 1;
    public static void main(String[] args) throws InterruptedException {
        long start=System.currentTimeMillis();
        final Semaphore semaphore = new Semaphore(threadCount);
        for (int i = 0; i < threadCount; i++){
            new Task(semaphore, start).start();
        }
    }
    private static class Task extends Thread {
        int result;
        long start;
        private Semaphore semaphore;

        public Task(Semaphore semaphore, long start) {
            this.semaphore = semaphore;
            this.start = start;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();
                result = sum();
                semaphore.release();
                System.out.println("异步计算结果为："+result);
                System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
