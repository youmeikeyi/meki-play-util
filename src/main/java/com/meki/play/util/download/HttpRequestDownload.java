/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util.download;

import com.google.common.collect.Lists;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.SSLException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpRequest下载服务
 *
 * @author Administrator
 */
public class HttpRequestDownload implements Callable<HttpRequestInfo> {

    public static final String DEFAULT_CHARSET = PageCharset.UTF_8_LOWER.getValue();

    private static final Logger LOG = LoggerFactory.getLogger(HttpRequestDownload.class);

    private int id;

    private List<String> urls;

    private String basePath;

    private String filePath;

    private HttpClient httpClient = HttpCilentUtils.createHttpClient(false);

    private HttpClient httpsClient = HttpCilentUtils.createHttpClient(true);

    public HttpRequestDownload(List<String> urls, String basePath, int id) {
        this.id = id;
        this.urls = urls;
        this.basePath = basePath;
        this.filePath = basePath + "/" + id + "/";
        init();
    }

    public static String parsePageCharset(String content) {

        int index = content.indexOf("charset");

        if (index == -1) {
            return PageCharset.UTF_8_UPPER.getValue();
        } else {
            String temp = content.substring(index + 8, content.indexOf(">", index + 8));
            return PageCharset.getCharsetByContent(temp);
        }

    }

    public static void process(List<String> urls, int threadNum, String basePath)
            throws InterruptedException, ExecutionException, IOException {

        ExecutorService exe = Executors.newFixedThreadPool(threadNum);

        List<Future<HttpRequestInfo>> requestInfos = Lists.newArrayListWithExpectedSize(threadNum);

        List<List<String>> urlGroups = Lists.partition(urls, urls.size() / threadNum + 1);

        for (int i = 0; i < threadNum; i++) {
            requestInfos.add(exe.submit(new HttpRequestDownload(urlGroups.get(i), basePath, i)));
        }
        exe.shutdown();

        BufferedWriter legalWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(basePath + "legal_urls.txt"), DEFAULT_CHARSET));
        BufferedWriter illegalWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(basePath + "illegal_urls.txt"), DEFAULT_CHARSET));

        for (Future<HttpRequestInfo> futureInfo : requestInfos) {
            HttpRequestInfo info = futureInfo.get();

            List<String> legalUrls = info.getLegalUrls();
            for (String url : legalUrls) {
                legalWriter.write(url);
                legalWriter.newLine();
            }

            List<String> illlegalUrls = info.getIllegalUrls();

            for (String url : illlegalUrls) {
                illegalWriter.write(url);
                illegalWriter.newLine();
            }

        }

        legalWriter.close();
        illegalWriter.close();

    }

    private void init() {
        File root = new File(filePath);
        if (root.exists()) {
            return;
        }
        root.mkdir();
    }

    public HttpRequestInfo call() throws Exception {

        HttpRequestInfo info = new HttpRequestInfo();
        List<String> illegalUrls = Lists.newArrayList();
        @SuppressWarnings("unused")
        List<String> legalUrls = Lists.newArrayList();

        int i = 0;

        for (String url : urls) {
            try {
                BufferedWriter writer = null;
                try {

                    HttpResponse httpResponse;
                    HttpClient client = httpClient;
                    try {
                        if (url.contains("https")) {
                            client = httpsClient;
                        }
                        httpResponse = client.execute(new HttpGet(url));
                    } catch (SSLException e) {
                        httpResponse = httpsClient.execute(new HttpGet(url.replace("http", "https")));
                    } catch (ClientProtocolException e) {
                        if (url.contains("https")) {
                            httpResponse = httpsClient.execute(new HttpGet(url.replace("https", "http")));
                        } else {
                            httpResponse = httpsClient.execute(new HttpGet(url.replace("http", "https")));
                        }

                    }

                    // Http Status
                    int status = httpResponse.getStatusLine().getStatusCode();
                    if (status != HttpStatus.SC_OK) {
                        illegalUrls.add(url);
                        continue;
                    }
                    // Http Entity
                    HttpEntity entity = httpResponse.getEntity();

                    String originContent =
                            IOUtils.toString(new InputStreamReader(entity.getContent(), DEFAULT_CHARSET));
                    LOG.info(originContent);
                    String charset = parsePageCharset(originContent);
                    if (charset == null) {
                        charset = DEFAULT_CHARSET;
                    }
                    LOG.info("");
                    // String finalContent = new String(originContent.getBytes(DEFAULT_CHARSET), charset);
                    String finalContent =
                            IOUtils.toString(new InputStreamReader(entity.getContent(), charset));
                    LOG.info(finalContent);
                    writer = new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(filePath + i + ".html"), charset));

                    writer.write(finalContent);
                    legalUrls.add(url);
                } catch (Exception e) {
                    illegalUrls.add(url);
                    e.printStackTrace();
                    continue;
                } finally {
                    if (writer != null) {
                        writer.close();
                    }
                }

                i++;

            } catch (Exception e) {
                illegalUrls.add(url);
                e.printStackTrace();
                continue;
            }

        }
        info.setLegalUrls(legalUrls);
        info.setIllegalUrls(illegalUrls);

        return info;

    }

}
