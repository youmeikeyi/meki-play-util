/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util;

import com.google.common.base.Splitter;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by xujinchao on 17/9/25.
 */
public class SplitUtil {

    private static String DEFAULT_SPLITTER = "|";

    public static Splitter getSplitterBySeparator(String separator) {
        if (StringUtils.isBlank(separator)) {
            separator = DEFAULT_SPLITTER;
        }
        return Splitter.on(separator).omitEmptyStrings().trimResults();
    }

    public static List<String> getBySeparator(String origin, String separator) {
        return getSplitterBySeparator(separator).splitToList(origin);
    }

}
