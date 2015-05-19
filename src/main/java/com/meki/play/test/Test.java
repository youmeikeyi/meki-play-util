package com.meki.play.test;

/**
 * Created by xujinchao on 2015/5/15.
 */
public class Test {

    public static void main(String[] args){
        String result = "success\r\n";
        System.out.println("success".equals(result));

        String nowTime = System.currentTimeMillis() / 1000 +"";
        System.out.println(nowTime.length());
        System.out.println((Long) System.currentTimeMillis());
    }
}
