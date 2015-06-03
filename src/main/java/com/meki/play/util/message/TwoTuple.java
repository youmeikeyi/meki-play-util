package com.meki.play.util.message;

import com.alibaba.fastjson.JSONObject;

/**
 * 数据传输对象
 * 需要多种类型参数，extends
 * Created by xujinchao on 2015/6/3.
 */
public class TwoTuple<A, B> {
    public final A first;
    public final B secound;

    public TwoTuple(A a, B b) {
        this.first = a;
        this.secound = b;
    }

    public String toString(){
        return "[first="+ first +"], [second=" + secound + "]";
    }

    public JSONObject toJsonString() {
        JSONObject result = new JSONObject();
        result.put("first", first);
        result.put("second", secound);

        return result;
    }
}
