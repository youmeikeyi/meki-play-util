package com.meki.play.test.spi;

import java.util.List;

/**
 * Created by xujinchao on 2016/2/24.
 */
public class DatabaseSearch implements SearchInterface {

    @Override
    public List<String> process(String keyword) {
        System.out.println("use " + getClass() + ", keyword: " + keyword);
        return null;
    }
}
