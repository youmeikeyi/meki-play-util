package com.meki.play.test;

import java.util.ArrayList;

/**
 * 适配器设计模式的一个实例
 * 将Generator适配到Collection的构造器上
 * a collection filled with data using a generator obejct
 * Created by xujinchao on 2015/7/6.
 */
public class CollectionData<T> extends ArrayList<T> {
    public CollectionData(Generator<T> gen, int quantity) {
        for (int i =0; i< quantity; i++) {
            add(gen.next());
        }
    }

    /**
     * 泛型便利方法
     * @param generator
     * @param quantity
     * @param <T>
     * @return
     */
    public static <T> CollectionData<T> list(Generator<T> generator, int quantity) {
        return new CollectionData<T>(generator, quantity);
    }
}
