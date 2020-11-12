package com.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.*;

public class FutureDemo {
    public static void main(String[] args) {
        long start=System.currentTimeMillis();
        Task task = new Task();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<String> future = new FutureTask<>(task,"calculation has finished");
        executor.submit(future);
        executor.shutdown();
        try{
            future.get(1000, TimeUnit.MINUTES);
            System.out.println("异步计算结果为："+task.result);
            System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");

        } catch(Exception e){
            e.printStackTrace();
        }

    }
    private static class Task implements Runnable{
        int result;
        @Override
        public void run() {
            result = sum();
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
