/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util.download;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpClient工具类
 *
 * @author Administrator
 */
public class HttpCilentUtils {

    static final String HTTP_HEADER = "http://";
    static final String HTTPS_HEADER = "https://";
    private static final Logger LOG = LoggerFactory.getLogger(HttpCilentUtils.class);
    private static final int TIME_OUT = 5000;
    // 读取超时
    private static final int SOCKET_TIMEOUT = 10000;
    // 连接超时
    private static final int CONNECTION_TIMEOUT = 10000;
    // 每个HOST的最大连接数量
    private static final int MAX_CONN_PRE_HOST = 20;
    // 连接池的最大连接数
    private static final int MAX_CONN = 60;
    public static String USER_AGENT = "Mozilla/5.0(Windows;U;Windows NT 5.1;en-US;rv:0.9.4)";
    // 连接池
    public static HttpConnectionManager httpConnectionManager;
//    private static HttpClient httpClient = HttpCilentUtils.createHttpClient(false);
//    private static HttpClient httpsClient = HttpCilentUtils.createHttpClient(true);
    private static CloseableHttpClient httpCommonClient = HttpCilentUtils.createHttpCommonClient(false, true);
    private static RequestConfig defaultRequestConfig =
            RequestConfig.custom().setSocketTimeout(50000).setConnectTimeout(50000).setConnectionRequestTimeout(50000)
                    .build();

    static {
        httpConnectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = httpConnectionManager.getParams();
        params.setConnectionTimeout(CONNECTION_TIMEOUT);
        params.setSoTimeout(SOCKET_TIMEOUT);
        params.setDefaultMaxConnectionsPerHost(MAX_CONN_PRE_HOST);
        params.setMaxTotalConnections(MAX_CONN);
    }

    public static final boolean isRedirect(int code) {
        return code >= 300 && code <= 307;
    }

    public static final boolean isSuccess(int code) {
        return code >= 200 && code <= 207;
    }

    public static final boolean isHttps(final String url) {
        if (url.contains(HTTPS_HEADER)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 对不包含协议头的url进行格式化
     *
     * @param url 网站url
     *
     * @return 添加http头后的url
     */
    public static String formatHttpUrl(final String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        if (!url.contains(HTTPS_HEADER) && !url.contains(HTTP_HEADER)) {
            return HTTP_HEADER + url;
        }
        return url;
    }

    public static RequestConfig createConfig(int timeout, boolean redirectsEnabled) {
        return RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setRedirectsEnabled(redirectsEnabled)
                .setCircularRedirectsAllowed(true)
                .build();
    }

    public static String circularRedirect(String url) {

        String url0 = formatHttpUrl(url);
        CloseableHttpClient httpClient = createHttpClient(false);
        CloseableHttpResponse httpResponse = null;
        while (true) {
            HttpGet httpGet = new HttpGet(url0);
            try {
                httpResponse = httpClient.execute(httpGet);

                if (isRedirect(httpResponse.getStatusLine().getStatusCode())) {
                    url0 = httpResponse.getFirstHeader("location").getValue();
                    LOG.info("redirect to url: " + url0);
                    httpGet.abort();
                    continue;
                }
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    return url0;
                }
                return null;
            } catch (ClientProtocolException e) {
                LOG.error("{}", e);
                return null;
            } catch (IOException e) {
                LOG.error("{}", e);
                return null;
            } finally {
                try {
                    if (httpResponse != null) {
                        httpResponse.close();;
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    public static CloseableHttpClient createHttpClient(boolean isHttps) {

        if (isHttps) {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");

                // set up a TrustManager that trusts everything
                sslContext.init(null, new TrustManager[] {new X509TrustManager() {
                    public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) {

                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) {
                        // TODO Auto-generated method stub

                    }

                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        // TODO Auto-generated method stub
                        return new java.security.cert.X509Certificate[1];
                    }
                } }, new SecureRandom());

                SSLSocketFactory sf = new SSLSocketFactory(sslContext);

                HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
                sf.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);

                SchemeRegistry schemeRegistry = new SchemeRegistry();

                Scheme httpsScheme = new Scheme("https", 443, sf);
                schemeRegistry.register(httpsScheme);

                // apache HttpClient version >4.2 should use
                // BasicClientConnectionManager
                //                ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
                //                ClientConnectionManager cm1 = new BasicHttpClientConnectionManager();
                //                PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
                //                cm.setMaxTotal(200);
                //                cm.setDefaultMaxPerRoute(20);
                // CloseableHttpClient httpclient
                // = new DefaultHttpClient(new ThreadSafeClientConnManager(schemeRegistry));

                return HttpClients.custom().setDefaultRequestConfig(createConfig(3000, true))
                        .setSSLSocketFactory(sf).setSchemePortResolver(DefaultSchemePortResolver.INSTANCE)
                        .disableContentCompression()
                        .setUserAgent(USER_AGENT)
                        .build();

            } catch (KeyManagementException e) {
                LOG.error(e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                LOG.error(e.getMessage());
            }
        }
        return HttpClients.custom().setDefaultRequestConfig(createConfig(3000, true))
                .setSchemePortResolver(DefaultSchemePortResolver.INSTANCE)
                .disableContentCompression()
                .setUserAgent(USER_AGENT)
                .build();
    }

    /**
     * 创建httpClient
     *
     * @param autoRedirect 是否自动重定向
     *
     * @return
     */
    public static CloseableHttpClient createHttpCommonClient(boolean autoRedirect,
                                                             boolean hasHttpClientConnectionManager) {

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");

            // set up a TrustManager that trusts everything
            sslContext.init(null, new TrustManager[] {new X509TrustManager() {
                public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) {
                }

                public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) {
                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[1];
                }
            } }, new SecureRandom());

            SSLSocketFactory sf = new SSLSocketFactory(sslContext);

            HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            sf.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);

            SchemeRegistry schemeRegistry = new SchemeRegistry();

            Scheme httpsScheme = new Scheme("https", 443, sf);
            schemeRegistry.register(httpsScheme);

            // apache HttpClient version >4.2 should use
            // BasicClientConnectionManager
            //            ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
            if (hasHttpClientConnectionManager) {
                PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
                cm.setMaxTotal(100);
                cm.setDefaultMaxPerRoute(20);
                return HttpClients.custom().setDefaultRequestConfig(createConfig(10000, autoRedirect))
                        .setSSLSocketFactory(sf).setSchemePortResolver(DefaultSchemePortResolver.INSTANCE)
                        .setUserAgent(USER_AGENT).setConnectionManager(cm).build();
            }
            return HttpClients.custom().setDefaultRequestConfig(createConfig(3000, autoRedirect))
                    .setSSLSocketFactory(sf).setSchemePortResolver(DefaultSchemePortResolver.INSTANCE)
                    .disableContentCompression().setUserAgent(USER_AGENT).build();
        } catch (KeyManagementException e) {
            LOG.error(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return new DefaultHttpClient();
    }

    /**
     * Http的Get请求指定参数
     *
     * @param url
     * @param params
     *
     * @return
     */
    public static String doGetWithParams(String url, List<NameValuePair> params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
        CloseableHttpResponse response = null;
        InputStream inputStream = null;

        // 封装请求参数
        String requestParams = "";

        StringBuilder stringBuilder = new StringBuilder();
        try {
            // 转换为键值对
            requestParams = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
            LOG.debug(requestParams);
            url = StringUtils.isEmpty(requestParams) ? url : url + "?" + requestParams;
            // 创建Get请求
            HttpGet httpGet = new HttpGet(url);
            // 执行Get请求
            response = httpClient.execute(httpGet);
            // 得到响应体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                inputStream = entity.getContent();
                // 转换为字节输入流
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Consts.UTF_8));
                String content = null;
                while ((content = bufferedReader.readLine()) != null) {
                    stringBuilder.append(content);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);

        } finally {
            // 关闭输入流，释放资源
            close(inputStream);
            // 消耗实体内容
            close(response);
            // 关闭相应 丢弃http连接
            close(httpClient);
        }
        return stringBuilder.toString();
    }

    /**
     * 关闭输入输出流等资源
     *
     * @param closeable 可关闭的资源
     */
    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

    }

    public static List<String> doGet(String url, List<NameValuePair> params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
        CloseableHttpResponse response = null;
        InputStream inputStream = null;

        // 封装请求参数
        String requestParams = "";

        List<String> contentList = Lists.newArrayList();
        try {
            // 转换为键值对
            requestParams = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
            LOG.debug(requestParams);
            // 创建Get请求
            HttpGet httpGet = new HttpGet(url + "?" + requestParams);
            // 执行Get请求
            response = httpClient.execute(httpGet);
            // 得到响应体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                inputStream = entity.getContent();
                // 转换为字节输入流
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Consts.UTF_8));
                String content = null;
                while ((content = bufferedReader.readLine()) != null) {
                    contentList.add(content);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭输入流，释放资源
            close(inputStream);
            // 消耗实体内容
            close(response);
            // 关闭相应 丢弃http连接
            close(httpClient);

        }
        return contentList;
    }

    public static String doGet(String url, Map<String, String> header) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }
        String result = "";
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            result = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return result;
    }

    public static HttpEntity getHttpEntity(String url) {

        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse httpResponse = httpCommonClient.execute(httpGet);
            return httpResponse.getEntity();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 请求内容为json格式的post请求
     *
     * @param url         请求链接
     * @param requestJson 请求内容(json格式)
     *
     * @return post请求返回字符串
     */
    public static String getByPostJsonParams(String url, JSONObject requestJson) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
        httpPost.setHeader("Charset", "UTF-8");
        StringEntity entity = new StringEntity(requestJson.toString().replace("\\", ""),
                ContentType.create("application/json", "UTF-8"));
        httpPost.setEntity(entity);

        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);

            if (httpResponse.getStatusLine().getStatusCode() != org.apache.http.HttpStatus.SC_OK) {
                throw new RuntimeException("getByPostJsonParams request failed");
            }
            String result = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
            LOG.info(result);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    public static CloseableHttpResponse getResponse(String parseUrl) throws Exception {
        CloseableHttpResponse httpResponse = null;
        HttpGet httpGet = null;
        CloseableHttpClient client = httpCommonClient;
        try {
            if (parseUrl.startsWith("https")) {
                parseUrl = parseUrl.replace("https", "http");
            }

            URL realUrl = new URL(parseUrl);
            URI uri = new URI(realUrl.getProtocol(), realUrl.getHost(), realUrl.getPath(), realUrl.getQuery(), null);
            httpGet = new HttpGet(uri);
            httpResponse = client.execute(httpGet);
        } catch (Exception e) {
            throw new Exception(e);
        }
        return httpResponse;
    }

    public static String parseCharset(String url) {

        String charset = PageCharset.UTF_8_LOWER.getValue();
        try {

            HttpResponse httpResponse = getResponse(url);

            // Http Status
            if (httpResponse == null) {
                return StringUtils.EMPTY;
            }

            int status = httpResponse.getStatusLine().getStatusCode();

            if (status != org.apache.http.HttpStatus.SC_OK) {
                return StringUtils.EMPTY;
            }
            // Http Entity
            HttpEntity entity = httpResponse.getEntity();

            String result = null;
            try {
                result = IOUtils.toString(new InputStreamReader(entity.getContent(), charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (StringUtils.isBlank(result)) {
                return StringUtils.EMPTY;
            }

            int index = result.indexOf("charset");

            if (index == -1) {
                return charset;
            } else {
                String temp = result.substring(index + 8, result.indexOf(">", index + 8));
                if (PageCharset.PAGE_CHARSET_MAP.keySet().contains(temp)) {
                    charset = PageCharset.getCharsetByContent(temp);
                }
                return charset;
            }

        } catch (Exception e) {
            return StringUtils.EMPTY;
        }

    }

    public static String doPost(String url, Map<String, String> header, Map<String, String> params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                nameValuePair.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, Consts.UTF_8));
        String result = "";
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            result = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return result;
    }

    public static String getHttpsReturnWhileHappensSSLException(String httpsUrl) {
        StringBuffer sb = new StringBuffer();
        try {
            SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());

            init();
            HttpsURLConnection.getDefaultSSLSocketFactory();

            // First set the default cookie manager.
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

            URL url = new URL(null, httpsUrl, new sun.net.www.protocol.http.Handler());
            //            URL url = new URL(httpsUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.addRequestProperty("User-Agent", USER_AGENT);
            List<String> cookies = urlConnection.getHeaderFields().get("Set-Cookie");

            // Then use the same cookies on all subsequent requests.
            urlConnection = new URL(httpsUrl).openConnection();
            if (CollectionUtils.isNotEmpty(cookies)) {
                for (String cookie : cookies) {
                    urlConnection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
                }
            }

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();

            if (urlConnection.getContentLengthLong() > HttpDownloadUtils.MAX_HTTP_CONTENT_SIZE) {
                return sb.toString();
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return sb.toString();
    }

    public static void init() {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[] {new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType)

                        throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[1];
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                    // TODO Auto-generated method stub

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                    // TODO Auto-generated method stub

                }
            } }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

            });
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * 输出当前jvm支持的TSL版本
     *
     * @param args
     *
     */
    public static void main(String[] args)  {
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

//        SSLContext context = SSLContext.getInstance("TLS");
//        context.init(null, null, null);
//
//        javax.net.ssl.SSLSocketFactory factory = context.getSocketFactory();
//        SSLSocket socket = (SSLSocket) factory.createSocket();
//
//        String[] protocols = socket.getSupportedProtocols();
//
//        System.out.println("Supported Protocols: " + protocols.length);
//        for (int i = 0; i < protocols.length; i++) {
//            System.out.println(" " + protocols[i]);
//        }
//
//        protocols = socket.getEnabledProtocols();
//
//        System.out.println("Enabled Protocols: " + protocols.length);
//        for (int i = 0; i < protocols.length; i++) {
//            System.out.println(" " + protocols[i]);
//        }

    }
}
