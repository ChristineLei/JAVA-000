package com.main;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {
    public static void main(String[] args) {
        long start=System.currentTimeMillis();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(1, new Runnable() {
            @Override
            public void run() {
                System.out.println("回调>>"+Thread.currentThread().getName());
                System.out.println("回调>>线程组执行结束");
            }
        });
        new Runnable(){
            public void run(){
                synchronized (this){
                    try {
                        int result = sum();
                        System.out.println("异步计算结果为："+result);
                        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
                        cyclicBarrier.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.run();
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
