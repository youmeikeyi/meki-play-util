package com.meki.play.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 并发Map
 * 简单封装了一层 修改了默认并发级别
 * Created by xujinchao on 2015/5/19.
 */
public class Misc {

    private static Misc misc = new Misc();
    private volatile int concurrencyLevel = 256;

    public Misc() {}

    public static Misc getInstance() {
        return misc;
    }

    public synchronized void setConcurrencyLevel(int level){
        this.concurrencyLevel = level;
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
        return new ConcurrentHashMap<K, V>(16, 0.75F, misc.concurrencyLevel);
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentMap(int initCapacity) {
        return new ConcurrentHashMap<K, V>(initCapacity, 0.75F, misc.concurrencyLevel);
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentMap(int initCapacity, float loadFactor, int level) {
        return new ConcurrentHashMap<K, V>(initCapacity, loadFactor, level);
    }
}
