package com.main;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {
    private final static int threadCount = 1;
    public static void main(String[] args) {
        long start=System.currentTimeMillis();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Task(cyclicBarrier,start).start();
        }
    }
    private static class Task extends Thread {
        int result;
        long start;
        private CyclicBarrier cyclicBarrier;

        public Task(CyclicBarrier cyclicBarrier, long start) {
            this.cyclicBarrier = cyclicBarrier;
            this.start = start;
        }
        @Override
        public void run() {
            try {
                result = sum();
                System.out.println("异步计算结果为："+result);
                System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
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
