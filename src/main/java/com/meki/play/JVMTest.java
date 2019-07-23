/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujinchao on 18/8/7.
 */
public class JVMTest {

    public static void main(String[] args) {

        List<String> list = new ArrayList<String>();

        int count = 0;
        while (true) {
            list.add(new String("" + (count++)));
        }
    }
}
