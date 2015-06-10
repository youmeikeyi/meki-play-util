package com.meki.play.test;

/**
 * Generator生成随机的Coffee对象，可继承
 * Created by xujinchao on 2015/6/3.
 */
public class Coffee {

    private static long counter = 0;
    private final long id = counter ++ ;

    public String toString(){
        return getClass().getSimpleName() + " " + id;
    }
}
