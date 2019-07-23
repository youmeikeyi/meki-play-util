/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util;

import com.google.common.collect.Lists;

import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * url转码工具类
 * Created by Administrator on 2017/11/23.
 */
public class URLDecodeUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(URLDecodeUtils.class);

    private static final String HTTP_PREFIX = "http";

    /**
     * 正则表达式
     * 过滤空格、回车、换行符、制表符
     */
    public static final String FILTER_REGEX = "[\\s*\t\n\r]";

    /**
     * 对进行过编码的url进行解码
     *
     * @param url url
     *
     * @return 解码后的url
     */
    public static String decodeURL(String url) {
        url = URLDecodeUtils.ascii2native(url);

        url = url.replaceAll(FILTER_REGEX, "");
        if (url != null && url.contains("%")) {
            try {
                url = url.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                url = url.replaceAll("\\+", "%2B");
                url = URLDecoder.decode(url, "utf-8");
            } catch (Exception e) {
                LOGGER.error("url {} decode failed", url, e);
            }
        }
        return url;
    }

    /**
     * 解析出有效的广告链接（针对url中包含多个http协议）
     *
     * @param url 原始url
     *
     * @return 解析后的url
     */
    public static String parseValidUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        // 未找到出现两次
        if (url.indexOf(HTTP_PREFIX, 7) == -1) {
            return url;
        }

        int lastIndexOf = url.lastIndexOf(HTTP_PREFIX);
        return url.substring(lastIndexOf);
    }

    /**
     * unicode编码转换
     *
     * @param ascii
     *
     * @return
     */
    public static String ascii2native(String ascii) {
        List<String> asciiList = Lists.newArrayList();

        String regex = "\\\\u[0-9,a-f,A-F]{4}";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(ascii);

        while (matcher.find()) {
            asciiList.add(matcher.group());
        }

        for (int i = 0, j = 2; i < asciiList.size(); i++) {
            String code = asciiList.get(i).substring(j, j + 4);
            char ch = (char) Integer.parseInt(code, 16);
            ascii = ascii.replace(asciiList.get(i), String.valueOf(ch));
        }

        return ascii;
    }
}
