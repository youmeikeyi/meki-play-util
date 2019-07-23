/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util.download;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xujinchao on 2018/8/3.
 */
public class HttpClientFactory {
    public static final Logger LOG = LoggerFactory.getLogger(HttpClientFactory.class);

    private static PoolingHttpClientConnectionManager poolConnectionManager = null;

    // pool max connection nums
    private static final Integer MAX_TOTAL = 500;

    private static final Integer MAX_PER_ROUTE = 20;

    // request time out time in ms
    private static final Integer REQ_TIMEOUT = 5 * 1000;
    // connect time out time in ms
    private static final Integer CONN_TIMEOUT = 5 * 1000;
    // socket time out in ms
    private static final Integer SOCK_TIMEOUT = 10 * 1000;

    // http connection manager thread
    private static HttpClientConnectionMonitorThread thread;

    private static CloseableHttpClient httpClient;

    private static RequestConfig requestConfig;

    static {
        try {
            LOG.info("PoolingHttpClientConnectionManager init begin...");
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());

            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(builder.build());

            // config to support http and https
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", socketFactory).build();
            // init pool connection manager
            poolConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

            poolConnectionManager.setMaxTotal(500);
            // 默认每个路由最高20并发，具体依业务而定
            poolConnectionManager.setDefaultMaxPerRoute(50);

            requestConfig = getRequestConfig();

            // inint httpClient
            httpClient = getConnection();

            LOG.info("PoolingHttpClientConnectionManager init end...");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

    }

    public static HttpClientConnectionMonitorThread getThread() {
        return thread;
    }

    public static void setThread(HttpClientConnectionMonitorThread thread) {
        HttpClientFactory.thread = thread;
    }

    public static HttpClient createSimpleHttpClient() {
        SSLConnectionSocketFactory sf = SSLConnectionSocketFactory.getSocketFactory();
        return HttpClientBuilder.create()
                .setSSLSocketFactory(sf)
                .build();
    }

    public static HttpClient createHttpClient() {
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager =
                new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(MAX_TOTAL);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(REQ_TIMEOUT)
                .setConnectTimeout(CONN_TIMEOUT).setSocketTimeout(SOCK_TIMEOUT)
                .build();

        HttpClientFactory.thread =
                new HttpClientConnectionMonitorThread(poolingHttpClientConnectionManager);
        HttpClientFactory.thread.start();
        return HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig).build();
    }

    public static RequestConfig getRequestConfig() {
        int socketTimeout = 1000;
        int connectTimeout = 1000;
        int connectionRequestTimeout = 1000;
        return RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout)
                .build();
    }

    public static CloseableHttpClient getConnection() {
        // when a thread request httpClient.close() method, other threads cannot use this to send http request.
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(poolConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                .build();

        if (poolConnectionManager != null && poolConnectionManager.getTotalStats() != null) {
            LOG.info("Now client pool " + poolConnectionManager.getTotalStats().toString());
        }
        return httpClient;
    }

    /**
     * send httpGet request
     *
     * @param url url
     *
     * @return return content
     */
    public static String httpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        String result = StringUtils.EMPTY;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, CharsetParse.UTF_8_L);
            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            HttpCilentUtils.close(response);
        }
        return result;
    }

    static class GetThread extends Thread {
        private CloseableHttpClient httpClient;
        private String url;

        public GetThread(CloseableHttpClient client, String url) {
            httpClient = client;
            this.url = url;
        }

        public void run() {
            for (int i = 0; i < 3; i++) {
                HttpGet httpGet = new HttpGet(url);
                CloseableHttpResponse response = null;
                try {
                    response = httpClient.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    String result = EntityUtils.toString(entity, "utf-8");
                    // EntityUtils.consume(entity);
                    System.out.println(Thread.currentThread().getName() + " Finished");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (response != null) {
                            response.close();
                        }
                        if (httpGet != null) {
                            httpGet.releaseConnection();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

        }
    }

    public static void main(String[] args) {
        HttpClientFactory
                .httpGet("https://kmg343.gitbooks.io/httpcl-ient4-4-no2/content/233_lian_jie_chi_guan_li_qi.html");
        String[] urisToGet = {
                "https://kmg343.gitbooks.io/httpcl-ient4-4-no2/content/24_duo_xian_cheng_zhi_xing_qing_qiu.html",
                "https://kmg343.gitbooks.io/httpcl-ient4-4-no2/content/24_duo_xian_cheng_zhi_xing_qing_qiu.html",
                "https://kmg343.gitbooks.io/httpcl-ient4-4-no2/content/24_duo_xian_cheng_zhi_xing_qing_qiu.html",
                "https://kmg343.gitbooks.io/httpcl-ient4-4-no2/content/24_duo_xian_cheng_zhi_xing_qing_qiu.html",
                "https://kmg343.gitbooks.io/httpcl-ient4-4-no2/content/24_duo_xian_cheng_zhi_xing_qing_qiu.html"
        };

        GetThread[] threads = new GetThread[urisToGet.length];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new GetThread(httpClient, urisToGet[i]);
        }

        for (Thread tmp : threads) {
            tmp.start();
        }
    }
}