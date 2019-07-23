///*
// * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
// */
//package com.meki.play.util.download;
//
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.util.EntityUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * 网页编码解析
// *
// * @author Administrator
// */
//public class CharsetParse {
//
//    private static final Logger LOG = LoggerFactory.getLogger(CharsetParse.class);
//
//    static String UTF_8 = "UTF_8";
//
//    static String UTF_8_L = "utf-8";
//
//    static String GB2312 = "gb2312";
//
//    static String ENCODE_GBK = "GBK";
//
//    static String ENCODE_GBK_L = "gbk";
//
//    /**
//     * 获取url的编码格式
//     *
//     * @param url
//     *
//     * @return
//     */
//    public static String parseCharset(String url, PageBean pageBean) {
//        try {
//            HttpResponse httpResponse = getResponse(url, pageBean);
//            // Http Status
//            if (httpResponse == null) {
//                pageBean.setResultTypeEnum(ParseResultTypeEnum.PAGE_FAULT);
//                pageBean.setDetailTypeEnum(ParseResultTypeEnum.PAGE_REQUEST_FAULT);
//                return StringUtils.EMPTY;
//            }
//
//            int status = httpResponse.getStatusLine().getStatusCode();
//            pageBean.setHttpStatus(status);
//            if (status != HttpStatus.SC_OK) {
//                LOG.error(url + ":" + status);
//                return StringUtils.EMPTY;
//            }
//            // Http Entity
//            HttpEntity entity = httpResponse.getEntity();
//            // 返回内容大于100M则判定为无效广告
//            if (entity.getContentLength() > HttpDownloadUtils.MAX_HTTP_CONTENT_SIZE
//                    || url.contains(HttpDownloadUtils.APK)
//                    || entity.getContentType() != null
//                    && ("application/octet-stream").equals(entity.getContentType().getValue())) {
//                pageBean.setResultTypeEnum(ParseResultTypeEnum.PAGE_CANNOT_PARSE);
//                pageBean.setDetailTypeEnum(ParseResultTypeEnum.PAGE_OVERLOAD);
//                return StringUtils.EMPTY;
//            }
//            String result = null;
//            try {
//                result = IOUtils.toString(new InputStreamReader(entity.getContent(), UTF_8_L));
//            } catch (Exception e) {
//                LOG.error(url + ":" + e.getMessage(), e);
//                pageBean.setResultTypeEnum(ParseResultTypeEnum.PAGE_CANNOT_PARSE);
//                pageBean.setDetailTypeEnum(ParseResultTypeEnum.PAGE_CANNOT_READ);
//                return StringUtils.EMPTY;
//            }
//
//            if (StringUtils.isBlank(result)) {
//                pageBean.setResultTypeEnum(ParseResultTypeEnum.PAGE_CANNOT_PARSE);
//                pageBean.setDetailTypeEnum(ParseResultTypeEnum.PAGE_CANNOT_READ);
//                return StringUtils.EMPTY;
//            }
//
//            return getCharsetFromResult(result);
//
//        } catch (IllegalArgumentException e) {
//            LOG.error(e.getMessage(), e);
//            pageBean.setResultTypeEnum(ParseResultTypeEnum.PAGE_CANNOT_PARSE);
//            HttpDownloadUtils.fillExceptionInfo(pageBean, e);
//            return StringUtils.EMPTY;
//
//        }
//
//    }
//
//    public static synchronized String parseCharset(String url) {
//        CloseableHttpResponse httpResponse = null;
//        try {
//            httpResponse = getResponse(url);
//
//            // Http Status
//            if (httpResponse == null) {
//                return StringUtils.EMPTY;
//            }
//
//            int status = httpResponse.getStatusLine().getStatusCode();
//
//            if (status != HttpStatus.SC_OK) {
//                LOG.error(url + ":" + status);
//                return StringUtils.EMPTY;
//            }
//            // Http Entity
//            HttpEntity entity = httpResponse.getEntity();
//
//            // 返回内容大于100M则判定为无效广告
//            if (entity.getContentLength() > HttpDownloadUtils.MAX_HTTP_CONTENT_SIZE) {
//                return StringUtils.EMPTY;
//            }
//            String result = null;
//            try {
//                //                result = IOUtils.toString(new InputStreamReader(entity.getContent(), UTF_8_L));
//                result = EntityUtils.toString(entity, UTF_8_L);
//                EntityUtils.consume(entity);
//            } catch (Exception e) {
//                LOG.error(url + ":" + e.getMessage(), e);
//            } finally {
//                if (httpResponse != null) {
//                    try {
//                        httpResponse.close();
//                    } catch (Exception e) {
//                        LOG.error(e.getMessage(), e);
//                    }
//                }
//            }
//
//            if (StringUtils.isBlank(result)) {
//                return StringUtils.EMPTY;
//            }
//
//            return getCharsetFromResult(result);
//
//        } catch (IllegalArgumentException e) {
//            LOG.error(e.getMessage(), e);
//            return StringUtils.EMPTY;
//
//        }
//
//    }
//
//    /**
//     * 获取url请求的响应
//     *
//     * @param url
//     *
//     * @return
//     */
//    public static CloseableHttpResponse getResponse(String url) {
//        CloseableHttpResponse httpResponse = null;
//        CloseableHttpClient httpClient = HttpCilentUtils.createHttpCommonClient(true, false);
//        HttpGet httpGet = new HttpGet(url);
//        try {
//            httpResponse = httpClient.execute(httpGet);
//        } catch (Exception e) {
//            if (url.contains("https")) {
//                CloseableHttpClient httpsClient = HttpCilentUtils.createHttpClient(true);
//                try {
//                    httpResponse = httpsClient.execute(httpGet);
//                } catch (Exception e1) {
//                    LOG.error(url + ":" + e1.getMessage(), e1);
//                }
//            }
//        }
//
//        return httpResponse;
//    }
//
//    /**
//     * 获取url请求的响应
//     *
//     * @param url
//     *
//     * @return
//     */
//    public static HttpResponse getResponse(String url, PageBean pageBean) {
//        HttpGet httpGet = new HttpGet(url);
//
//        HttpClient httpClient = url.contains("https") ? HttpCilentUtils.createHttpClient(true) :
//                HttpCilentUtils.createHttpClient(false);
//        HttpResponse httpResponse = null;
//
//        try {
//            httpResponse = httpClient.execute(httpGet);
//        } catch (Exception e) {
//            LOG.error(url + ":" + e.getMessage(), e);
//            HttpDownloadUtils.fillExceptionInfo(pageBean, e);
//        }
//        return httpResponse;
//    }
//
//    /**
//     * 从网页请求获取编码－test
//     *
//     * @param url
//     * @param pageBean
//     *
//     * @return
//     */
//    public static String parseCharsetNew(String url, PageBean pageBean) {
//        String charset = "UTF-8";
//        String line = "";
//        StringBuffer buffer = new StringBuffer();
//        try {
//            URL url2 = new URL(url);
//            // 开始访问该URL
//            HttpURLConnection urlConnection = (HttpURLConnection) url2.openConnection();
//            // 获取服务器响应代码
//            int responsecode = urlConnection.getResponseCode();
//            String contentType = urlConnection.getContentType();
//            // 打印出content-type值,然后就可以从content-type中提取出网页编码
//            LOG.info("content-type:" + contentType);
//            pageBean.setCharSet(contentType);
//            pageBean.setHttpStatus(responsecode);
//
//            if (responsecode == HttpStatus.SC_OK) {
//                // 获取网页输入流
//                BufferedReader reader = new
//                        BufferedReader(new InputStreamReader(urlConnection.getInputStream(), charset));
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line).append("/n");
//                }
//                //                LOG.info(buffer.toString());
//            } else {
//                LOG.info("获取不到网页的源码,服务器响应代码为:" + responsecode);
//            }
//            urlConnection.disconnect();
//        } catch (Exception e) {
//            HttpDownloadUtils.fillExceptionInfo(pageBean, e);
//            LOG.error(e.getMessage(), e);
//        }
//
//        return charset;
//    }
//
//    /**
//     * 从http结果获取charset
//     *
//     * @param result
//     *
//     * @return
//     */
//    private static String getCharsetFromResult(String result) {
//        int index = result.indexOf("charset");
//
//        if (index == -1) {
//            return PageCharset.UTF_8_LOWER.getValue();
//        }
//        String temp = result.substring(index + 8, result.indexOf(">", index + 8));
//
//        String charset = UTF_8_L;
//        if (temp.contains(ENCODE_GBK)) {
//            charset = ENCODE_GBK;
//        } else if (temp.contains(ENCODE_GBK_L)) {
//            charset = ENCODE_GBK_L;
//        } else if (temp.contains(UTF_8_L)) {
//            charset = UTF_8_L;
//        } else if (temp.contains(GB2312)) {
//            charset = GB2312;
//        } else {
//            charset = UTF_8_L;
//        }
//        return charset;
//
//    }
//
//}
