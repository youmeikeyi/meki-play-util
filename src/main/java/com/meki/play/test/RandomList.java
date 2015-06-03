package com.meki.play.test;

import java.util.ArrayList;
import java.util.Random;

/**
 * 对象随机生成器
 * 一系列特殊类型的对象
 * Created by xujinchao on 2015/6/3.
 */
public class RandomList<T> {
    private ArrayList<T> storage = new ArrayList<T>();
    private Random random = new Random(47);

    public void add(T item) {
        this.storage.add(item);
    }

    public T select() {
        return storage.get(random.nextInt(storage.size()));
    }

    public static void main(String[] args) {

    }
}
