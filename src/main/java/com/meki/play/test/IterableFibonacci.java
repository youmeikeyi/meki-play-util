package com.meki.play.test;

import java.util.Iterator;

/**
 * 利用适配器模式实现
 * Created by xujinchao on 2015/6/3.
 */
public class IterableFibonacci extends Fibonacci implements Iterable<Integer> {

    //生成Fibonacci数的个数
    private int n;

    public IterableFibonacci(int count) {
        this.n = count;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            @Override
            public boolean hasNext() {
                return n > 0;
            }

            @Override
            public Integer next() {
                n --;
                return IterableFibonacci.this.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static void main(String[] args) {
        for(int i : new IterableFibonacci(18))
            System.out.print(i + " ");
    }
}
