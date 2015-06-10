package com.meki.play.test;

import java.util.Iterator;
import java.util.Random;

/**
 * Created by xujinchao on 2015/6/3.
 */
public class CoffeeGenerator implements Generator<Coffee>, Iterable<Coffee> {

    //Coffee子类
    private Class[] types = {Coffee.class};
    private static Random random = new Random();

    public CoffeeGenerator(){}

    //for iteration
    private int size = 0;
    public CoffeeGenerator(int sz){
        size = sz;
    }

    @Override
    public Coffee next() {
        try {
            return (Coffee) types[random.nextInt(types.length)].newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    class CoffeeIterator implements Iterator<Coffee>{
        int count = size;

        @Override
        public boolean hasNext() {
            return count > 0;
        }

        @Override
        public Coffee next() {
            count --;
            return CoffeeGenerator.this.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<Coffee> iterator() {
        return new CoffeeIterator();
    }

    public static void main(String[] args) {
        CoffeeGenerator gen = new CoffeeGenerator();
        for (int i = 0; i < 5; i ++ ) {
            System.out.println(gen.next());
        }
        for(Coffee c : new CoffeeGenerator(5))
            System.out.println(c);
    }
}
