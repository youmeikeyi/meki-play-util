package com.meki.play.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * java保护机制  快速报错
 * 防止并发修改
 * Created by xujinchao on 2015/7/8.
 */
public class FailFast {
    public static void main(String[] args) {
        Collection<String> collection = new ArrayList<String>();
        Iterator<String> iterator = collection.iterator();

        collection.add("hello");
        try {
            String x = iterator.next();
        } catch (ConcurrentModificationException cme) {
            System.out.println(cme);
        }
    }
}
