/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xujinchao on 18/3/23.
 */
public class Test {
    static class Num {
        int num = 0;
    }

    private static Lock lock = new ReentrantLock();
    private static Condition jCon = lock.newCondition();
    private static Condition oCon = lock.newCondition();

    static class ThreadA extends Thread {
        private Num num;

        public ThreadA(Num num) {
            this.num = num;
        }

        @Override
        public void run() {
            while (num.num <= 100) {
                try {
                    lock.lock();
                    while (num.num % 2 != 1) {
                        jCon.await();
                    }
                    System.out.println(num.num);
                    num.num++;
                    TimeUnit.SECONDS.sleep(3);
                    oCon.signal();
                } catch (Exception e) {

                } finally {
                    lock.unlock();
                }
            }

        }
    }

    static class ThreadB extends Thread {
        Num num;

        public ThreadB(Num num) {
            this.num = num;
        }

        @Override
        public void run() {
            while (num.num <= 100) {
                try {
                    lock.lock();
                    while (num.num % 2 != 0) {
                        oCon.await();
                    }
                    System.out.println(num.num);
                    num.num++;
                    TimeUnit.SECONDS.sleep(3);
                    jCon.signal();
                } catch (Exception e) {

                } finally {
                    lock.unlock();
                }
            }

        }
    }

    public static void main(String[] args) {
        Test test = new Test();
        Num num = new Num();
        num.num = 0;

        ThreadA threadA = new ThreadA(num);
        ThreadB threadB = new ThreadB(num);
        threadA.start();
        threadB.start();

    }
}
