package com.main;

public class WaitandNotify {

    public static void main(String[] args) {
        long start=System.currentTimeMillis();
        CalThread cal = new CalThread();
        cal.start();
        synchronized (cal) {
            try{
                cal.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("异步计算结果为："+cal.result);
            System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
        }
    }
    static class CalThread extends Thread{
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
