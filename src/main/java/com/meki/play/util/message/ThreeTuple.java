package com.meki.play.util.message;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by xujinchao on 2015/6/3.
 */
public class ThreeTuple<A, B, C> extends TwoTuple<A, B> {

    public final C third;

    public ThreeTuple(A a, B b, C c) {
        super(a, b);
        this.third = c;
    }

    @Override
    public String toString() {
        String str = ", [third=" + third + "]";
        return super.toString() + str;
    }

    @Override
    public JSONObject toJsonString() {
        JSONObject result =  new JSONObject(super.toJsonString());
        result.put("third", third);
        return result;
    }

    public static void main(String[] args){
//        TwoTuple twoTuple = new TwoTuple("", 2);
    }
}
