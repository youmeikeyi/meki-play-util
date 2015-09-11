package com.meki.play.test;

import java.util.*;

/**
 * 对象随机生成器
 * 一系列特殊类型的对象
 * Created by xujinchao on 2015/6/3.
 */
public class RandomList<T> {
    private ArrayList<T> storage = new ArrayList<T>();
    //使用固定的随机种子时得到的随机数是一定的，要么不用要么变种
    private Random random = new Random(System.currentTimeMillis());

    public void add(T item) {
        this.storage.add(item);
    }

    public T select() {
        return storage.get(random.nextInt(storage.size()));
    }

    public static void main(String[] args) {
//        RandomList<String> rs = new RandomList<String>();
//        for (String s : ("I have a dog! It's name dog!").split(" ")) {
//            rs.add(s);
//        }
//
//        for (int i = 0; i < 11; i++) {
//            System.out.print(rs.select() + " ");
//        }

        RandomList<Long> lr = new RandomList<Long>();

        long nowTime = System.currentTimeMillis();
        int count = 10000;
        TreeMap map = treeMapAdd(count, lr);
        long endTime = System.currentTimeMillis();
        System.out.println();
        System.out.println("TreeMap add " + map.size() + " long numbers, cost " + (endTime - nowTime) + " ms");

        long finalTime = System.currentTimeMillis();
        int result = treeMapRemove(lr, count, map);
        System.out.println();
        System.out.println("TreeMap remove " + result + " lefts " + map.size() + ", cost " + (finalTime - endTime) +
                " ms");
    }

    private static TreeMap treeMapAdd(int count, RandomList<Long> lr){
        Random random = new Random(System.currentTimeMillis());
        int index = 0;
        TreeMap map = new TreeMap();
        while (++index < count){
            long element = random.nextLong();
            lr.add(element);
            if (index < 10) {
                System.out.print(element + ", ");
            }
            map.put(element+"", element);
        }
        return map;
    }

    private static int treeMapRemove(RandomList<Long> lr, int num, TreeMap map){
        int index = 0;
        while (++index < num) {
            map.remove(lr.select() + "");
        }
        return index;
    }
}
