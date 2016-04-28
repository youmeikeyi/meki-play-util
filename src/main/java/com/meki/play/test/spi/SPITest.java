package com.meki.play.test.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by xujinchao on 2016/2/24.
 */
public class SPITest {

    public static void main(String[] args) {
        ServiceLoader<SearchInterface> serviceLoader = ServiceLoader.load(SearchInterface.class);
        Iterator<SearchInterface> searchs = serviceLoader.iterator();
        if (searchs.hasNext()) {
            SearchInterface curSearch = searchs.next();
            curSearch.process("test");
        }
    }
}
