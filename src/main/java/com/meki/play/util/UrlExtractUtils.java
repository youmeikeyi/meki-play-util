/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util;

import com.baidu.rigel.feed.download.HttpDownloadUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从URL中提取准确的Ad_Id
 *
 * @author wuliang07
 */
public class UrlExtractUtils {

    private static final Logger LOG = LoggerFactory.getLogger(UrlExtractUtils.class);

    /**
     * 处理广告url
     * 转换编码,去除多余项达到去重目的
     * 注意:是否影响抽取UrlAdId
     *
     * @param adUrl
     *
     * @return
     */
    public static String getCleanUrl(String adUrl) {
        // 转换unicode地址符
        adUrl = adUrl.replaceAll("\\\\u0026", "&");

        if (!adUrl.contains("http")) {
            return StringUtils.EMPTY;
        }

        adUrl = URLDecodeUtils.decodeURL(adUrl);
        adUrl = URLDecodeUtils.decodeURL(adUrl);
        adUrl = URLDecodeUtils.parseValidUrl(adUrl);

        // req_id推断为时间戳和其他信息组合而成,没有存储价值
        if (adUrl.contains("toutiao") && adUrl.contains("cid=")) {
            int index = adUrl.indexOf("&cid=");
            if (index != -1) {
                adUrl = adUrl.substring(0, index);
            }
        }

        // 处理
        if (adUrl.contains("&uc_biz_str")) {
            int index = adUrl.indexOf("&uc_biz_str");
            if (index != -1) {
                adUrl = adUrl.substring(0, index);
            }
            if (adUrl.contains("huichuan.sm.cn")) {
                // 提前获取真实url
                adUrl = HttpDownloadUtils.getLastRedirectLocation(adUrl);
            }
        }

        // UC广告中包含uctrackid=
        if (adUrl.contains("uctrackid=")) {
            int index = adUrl.indexOf("uctrackid=");
            if (index != -1) {
                adUrl = adUrl.substring(0, index);
            }
        }

        if (adUrl.contains("cdn.zampdsp.com") && adUrl.contains("?")) {
            int index = adUrl.indexOf("?");
            if (index != -1) {
                adUrl = adUrl.substring(0, index);
            }
        }

        // url中包含花括号,尝试去除参数
        if (adUrl.contains("{")) {
            int index = adUrl.indexOf("?");
            if (index != -1) {
                adUrl = adUrl.substring(0, index);
            }
        }
        return adUrl;
    }

    public static long extractUrlAdId(String url) {
        long adId = 0L;
        try {
            if (url.contains("toutiao") && url.contains("ad_id")) {
                url = url.replaceAll("\\\\u0026", "&");
                int startIndex = url.indexOf("ad_id");
                int endIndex = url.indexOf("&", startIndex) != -1 ? url.indexOf("&", startIndex) : url.length() - 1;
                String adIdStr = url.substring(startIndex + 6, endIndex);
                adId = Long.parseLong(adIdStr);

            }
        } catch (Exception e) {
            // StringIndexOutOfBoundsException
            LOG.info("UrlExtractUtils extractUrlAdId error:" + url);
        }
        return adId;
    }

}
