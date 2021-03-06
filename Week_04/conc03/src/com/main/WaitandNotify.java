package com.main;

public class WaitandNotify {

    public static void main(String[] args) {
        long start=System.currentTimeMillis();
        Task task = new Task();
        task.start();
        synchronized (task) {
            try{
                task.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("异步计算结果为："+task.result);
            System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
        }
    }
    static class Task extends Thread{
        int result;
        @Override
        public void run() {
            synchronized (this) {
                result = sum();
                notify();
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
