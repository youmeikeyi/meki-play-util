/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play;

public class DeadLockTest {
    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        Thread thread1 = new Thread(myThread, "thread-1");
        Thread thread2 = new Thread(myThread, "thread-2");
        thread1.start();
        thread2.start();
    }
    private static class MyThread implements Runnable {
        private Object object1 = new Object();
        private Object object2 = new Object();
        public void methodA() {
            synchronized (object1) {
                synchronized (object2) {
                    System.out.println("Thread:" + Thread.currentThread().getName() + "invoke methodA");
                }
            }
        }
        public void methodB() {
            synchronized (object2) {
                synchronized (object1) {
                    System.out.println("Thread:" + Thread.currentThread().getName() + "invoke methodB");
                }
            }
        }
        @Override
        public void run() {
            for(int i = 0; i < 1000; i++) {
                methodA();
                methodB();
            }
        }
    }
}
