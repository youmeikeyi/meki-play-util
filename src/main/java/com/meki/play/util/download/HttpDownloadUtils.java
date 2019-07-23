/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util.download;

import com.baidu.rigel.feed.common.enums.ParseResultTypeEnum;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http下载工具类
 *
 * @author Administrator
 */
public class HttpDownloadUtils {

    public static final Logger LOG = LoggerFactory.getLogger(HttpDownloadUtils.class);

    public static final String DEFAULT_CHARSET = "utf-8";

    public static final long MAX_HTTP_CONTENT_SIZE = 50 * 1024 * 1024;

    public static final String APK = ".apk";

    // 创建并配置HttpClient
    private static final CloseableHttpClient httpClient = HttpClients
            .custom()
            .setUserAgent(HttpCilentUtils.USER_AGENT)
            .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build())
            .build();

    /**
     * 获取网页编码，结果封装进bean
     * @param html
     * @param pageBean
     * @return
     */
    public static String parseCharset(String html, PageBean pageBean) {

        if (StringUtils.isBlank(html)) {
            LOG.info("html is null");
            return DEFAULT_CHARSET;
        }

        try {
            Parser parser = Parser.createParser(html, "utf-8");
            NodeFilter nodeFilter = new TagNameFilter("meta");
            NodeList metaNodeList = parser.extractAllNodesThatMatch(nodeFilter);
            // LOG.info("meta node list size: " + metaNodeList.size() +", nodelist:" + metaNodeList);
            for (int i = 0; i < metaNodeList.size(); i++) {
                Node currentNode = metaNodeList.elementAt(i);
                String text = currentNode.getText();
                // LOG.info(text);
                String charset = PageCharset.getCharsetByContent(text);
                if (StringUtils.isNotBlank(charset)) {
                    pageBean.setCharSet(charset);
                    return charset;
                }
            }
        } catch (ParserException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            fillExceptionInfo(pageBean, e);
        }
        pageBean.setCharSet(DEFAULT_CHARSET);
        return DEFAULT_CHARSET;
    }

    public static String parseCharset(String html) {

        if (StringUtils.isBlank(html)) {
            LOG.info("html is null");
            return DEFAULT_CHARSET;
        }

        try {
            Parser parser = Parser.createParser(html, "utf-8");
            NodeFilter nodeFilter = new TagNameFilter("meta");
            NodeList metaNodeList = parser.extractAllNodesThatMatch(nodeFilter);
            // LOG.info("meta node list size: " + metaNodeList.size() +", nodelist:" + metaNodeList);
            for (int i = 0; i < metaNodeList.size(); i++) {
                Node currentNode = metaNodeList.elementAt(i);
                String text = currentNode.getText();
                // LOG.info(text);
                String charset = PageCharset.getCharsetByContent(text);
                if (StringUtils.isNotBlank(charset)) {
                    return charset;
                }
            }
        } catch (ParserException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        return DEFAULT_CHARSET;
    }

    /**
     * 将URL返回结果封装成PageBean
     * 注意:解析url为文件下载时会终止
     *
     * @param url 广告链接
     *
     * @return http返回内容封装后的结果
     */
    public static synchronized PageBean parseHtml(String url) {

        String url0 = HttpCilentUtils.formatHttpUrl(url);

        PageBean pageBean = new PageBean();
        if (StringUtils.isEmpty(url0)) {
            pageBean.setResultTypeEnum(ParseResultTypeEnum.PAGE_FAULT);
            return pageBean;
        }

        int count = 0;

        CloseableHttpResponse httpResponse = null;
        while (true) {
            count ++;
            if (count > 1) {
                pageBean.setReason("Retry more than 3 times");
                return pageBean;
            }
            try {
                //                                url0 = URLEncoder.encode(url0, "UTF-8");
                URL urlTemp = new URL(url0);
                URI uri = new URI(urlTemp.getProtocol(), urlTemp.getHost(), urlTemp.getPath(), urlTemp.getQuery(), null);

                String ourl = uri.toString();
                HttpGet httpGet = new HttpGet(ourl);
                CloseableHttpClient httpClient = HttpCilentUtils.createHttpCommonClient(true, false);
                httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 "
                        + "(KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
                httpGet.setHeader("Referer", url0);
                httpResponse = httpClient.execute(httpGet);
                pageBean.setUrl(url0);
                pageBean.setHttpStatus(httpResponse.getStatusLine().getStatusCode());
                HttpEntity httpEntity = httpResponse.getEntity();
                if (HttpCilentUtils.isSuccess(httpResponse.getStatusLine().getStatusCode())) {
                    if (StringUtils.isNotEmpty(url0) && url0.contains(APK)
                            || httpEntity.getContentLength() > MAX_HTTP_CONTENT_SIZE
                            || httpEntity.getContentType() != null
                            && ("application/octet-stream").equals(httpEntity.getContentType().getValue())) {
                        pageBean.setResultTypeEnum(ParseResultTypeEnum.PAGE_OVERLOAD);
                        pageBean.setHtml("");
                        return pageBean;
                    }
                    String preHtml = IOUtils.toString(
                            new InputStreamReader(httpEntity.getContent(), DEFAULT_CHARSET));
                    String charset = parseCharset(preHtml, pageBean);
                    if (charset.equals(DEFAULT_CHARSET)) {
                        pageBean.setHtml(preHtml);
                        return pageBean;
                    } else {
                        httpResponse = httpClient.execute(httpGet);
//                        pageBean.setHtml(IOUtils.toString(
//                                new InputStreamReader(httpResponse.getEntity().getContent(), charset)));
                        pageBean.setHtml(EntityUtils.toString(httpResponse.getEntity(), charset));

                        return pageBean;
                    }

                } else {
                    pageBean.setResultTypeEnum(ParseResultTypeEnum.PAGE_FAULT);
                    return pageBean;
                }

            } catch (ClientProtocolException e) {
                // deal with circular redirect
                url0 = HttpCilentUtils.circularRedirect(url0);
                LOG.error(e.getMessage(), e);
                pageBean.setResultTypeEnum(ParseResultTypeEnum.PAGE_FAULT);

                fillExceptionInfo(pageBean, e);
            } catch (Exception e) {
                LOG.error("#:illegal url: " + url);
                LOG.error(e.getMessage(), e);
                pageBean.setResultTypeEnum(ParseResultTypeEnum.PAGE_FAULT);

                fillExceptionInfo(pageBean, e);
                return pageBean;
            } finally {
                if (httpResponse != null) {
                    try {
                        EntityUtils.consume(httpResponse.getEntity());
                        httpResponse.close();
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                    }

                }
            }
        }

    }

    /**
     * 获取重定向最终指向的地址
     *
     * @param link 链接
     *
     * @return 最终指向的真实地址
     */
    public static String getLastRedirectLocation(String link) {
        if (StringUtils.isEmpty(link)) {
            return StringUtils.EMPTY;

        }
        List<URI> uriList = null;
        try {
            uriList = getAllRedirectLocations(link);
        } catch (IOException e) {
            LOG.error("{},{}", e.getMessage(), e);
            return StringUtils.EMPTY;
        }
        if (CollectionUtils.isEmpty(uriList)) {
            return StringUtils.EMPTY;
        }
        URI lastUri = uriList.get(uriList.size() - 1);
        if (lastUri == null) {
            return StringUtils.EMPTY;
        }
        return lastUri.toASCIIString();
    }

    /**
     * 根据给定的链接获取所有的重定向位置
     *
     * @param link 给定的链接
     *
     * @return
     *
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static List<URI> getAllRedirectLocations(String link) throws IOException {
        List<URI> redirectLocations = null;
        CloseableHttpResponse response = null;
        try {
            HttpClientContext context = HttpClientContext.create();
            HttpGet httpGet = new HttpGet(link);
            CloseableHttpClient httpClient = HttpCilentUtils.createHttpCommonClient(true, false);
            response = httpClient.execute(httpGet, context);

            // 获取所有的重定向位置
            redirectLocations = context.getRedirectLocations();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return redirectLocations;
    }

    public static void main(String[] args) {
        // 输入URL
        String link =
                "http://c.gridsumdissector"
                        + ".com/r/?gid=gad_167_knb5dzy8&gsos=0&gsidfa=__IDFA__&gsopenudid=3a62f020b46b405e"
                        + "&gsandroidid=3a62f020b46b405e&gsimei=e431ed7b12a39c30728579deb1332827&gsmac=__MAC__&gsip"
                        + "=222.140.109.70&gsts=1519574450000&gsduid=__DUID__&gsaaid=__AAID__&gsudid=__UDID__&gsodin"
                        + "=__ODIN__&gsua=__UA__&gslbs=0.000000x-0.000000x100.0&gsreqid=[TENCENTSOID]";

        try {
            List<URI> allRedirectLocations = getAllRedirectLocations(link);
            if (allRedirectLocations != null) {
                System.out.println(link);
                for (URI uri : allRedirectLocations) {
                    System.out.println("|\nv\n" + uri.toASCIIString());
                }
            } else {
                System.out.println("Not found!");
            }

        } catch (IOException e) {
            LOG.error("", e.getMessage(), e);
        }

    }

    /**
     * 为pageBean填充异常信息
     *
     * @param pageBean
     * @param e
     */
    public static void fillExceptionInfo(PageBean pageBean, Exception e) {
        if (pageBean != null && e != null) {
            pageBean.setMsg(e.getMessage());
            pageBean.setReason(e.getClass().getName());
        }
    }

}
