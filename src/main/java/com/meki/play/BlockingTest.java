/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Java实现阻塞队列
 * 允许多个用户读,一个用户写
 * Created by xujinchao on 19/2/27.
 */
public class BlockingTest {

    Object wlock = new Object();
    Object rlock = new Object();

    ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    Lock readLock = reentrantReadWriteLock.readLock();;

    List<Integer> list = new ArrayList<>();

    public void add() {
        while (true) {
            try {
                synchronized(list) {
                    if (list.size() == 100) {
                        wlock.wait();
                    }
                    list.add(1);
                    rlock.notify();
                }
            } catch (Exception e) {

            } finally {

            }

        }

    }


    public void get(int index) {
        while (true) {
            try {

                list.get(index);

            } catch (Exception e) {

            } finally {

            }

        }
    }
}
