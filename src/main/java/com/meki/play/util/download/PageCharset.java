/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util.download;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 网页编码类型
 *
 * @author Administrator
 */
public enum PageCharset {

    UTF_8_UPPER("UTF-8", "utf-8"),

    UTF_8_LOWER("utf-8", "utf-8"),

    GB2312_LOWER("gb2312", "gb2312"),

    GB2312_UPPER("GB2312", "gb2312"),

    GBK_UPPER("ENCODE_GBK", "ENCODE_GBK_L"),

    GBK_LOWER("ENCODE_GBK_L", "ENCODE_GBK_L"),

    UTF8_UPPER("UTF8", "utf-8"),

    UTF8_LOWER("utf8", "utf-8"),

    ISO("iso-8859-1", "iso-8859-1");

    public static final Map<String, PageCharset> PAGE_CHARSET_MAP = Maps.newHashMap();

    static {
        for (PageCharset type : values()) {
            PAGE_CHARSET_MAP.put(type.getKey(), type);
        }
    }

    private String key;
    private String value;

    PageCharset(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getCharsetByContent(String content) {
        for (PageCharset key : values()) {
            if (content.contains(key.getKey())) {
                return key.getValue();
            }
        }
        return null;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
