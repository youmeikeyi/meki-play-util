package com.meki.play.test;

/**
 * Here’s a class that produces a Generator for any class that has a default constructor.
 * Created by xujinchao on 2015/6/8.
 */
public class BasicGenerator<T> implements Generator<T> {

    private Class<T> type;
    public BasicGenerator(Class<T> type){
        this.type = type;
    }

    @Override
    public T next() {
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
//            System.out.println();
        }
    }

    // Produce a Default generator given a type token:
    public static <T> Generator<T> create(Class<T> type) {
        return new BasicGenerator<T>(type);
    }
}
