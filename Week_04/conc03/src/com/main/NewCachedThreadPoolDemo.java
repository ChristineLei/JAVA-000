package com.main;

import java.lang.management.ThreadInfo;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NewCachedThreadPoolDemo {
    public static void main(String[] args) {
        long start=System.currentTimeMillis();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        Task task = new Task();
        executor.submit(task);
        executor.shutdown();
        try{
            while(!executor.isTerminated()){
                //System.out.println(Thread.currentThread().getName());
                Thread.sleep(10);
            }
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
