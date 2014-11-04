//package com.meki.play.util;
//
//import com.alibaba.fastjson.JSON;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.config.*;
//import org.apache.http.conn.socket.ConnectionSocketFactory;
//import org.apache.http.conn.socket.PlainConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLContexts;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.SocketTimeoutException;
//import java.net.URLEncoder;
//import java.nio.charset.CodingErrorAction;
//import java.security.KeyManagementException;
//import java.security.NoSuchAlgorithmException;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * User: jinchao.xu
// * Date: 2014/8/12
// * Time: 16:32
// */
//public class HttpClientUtilTest {
//    private static Logger LOGGER = LoggerFactory.getLogger(HttpClientUtilTest.class);
//
//    private static final String CHARSET_UTF8 = "UTF-8";
//    private static final String CHARSET_GBK = "GBK";
//    private static final String SSL_DEFAULT_SCHEME = "https";
//    private static final int SSL_DEFAULT_PORT = 443;
//
//    private static PoolingHttpClientConnectionManager connManager = null;
//    private static CloseableHttpClient httpclient = null;
//
//    static {
//        try {
//            SSLContext sslContext = SSLContexts.custom().useTLS().build();
//            sslContext.init(null,
//                    new TrustManager[]{new X509TrustManager() {
//
//                        public X509Certificate[] getAcceptedIssuers() {
//                            return null;
//                        }
//
//                        public void checkClientTrusted(
//                                X509Certificate[] certs, String authType) {
//                        }
//
//                        public void checkServerTrusted(
//                                X509Certificate[] certs, String authType) {
//                        }
//                    }}, null);
//            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
//                    .register("http", PlainConnectionSocketFactory.INSTANCE)
//                    .register("https", new SSLConnectionSocketFactory(sslContext))
//                    .build();
//
//            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
//            httpclient = HttpClients.custom().setConnectionManager(connManager).build();
//            // Create socket configuration
//            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
//            connManager.setDefaultSocketConfig(socketConfig);
//            // Create message constraints
//            MessageConstraints messageConstraints = MessageConstraints.custom()
//                    .setMaxHeaderCount(200)
//                    .setMaxLineLength(2000)
//                    .build();
//            // Create connection configuration
//            ConnectionConfig connectionConfig = ConnectionConfig.custom()
//                    .setMalformedInputAction(CodingErrorAction.IGNORE)
//                    .setUnmappableInputAction(CodingErrorAction.IGNORE)
//                    .setCharset(Consts.UTF_8)
//                    .setMessageConstraints(messageConstraints)
//                    .build();
//            connManager.setDefaultConnectionConfig(connectionConfig);
//            connManager.setMaxTotal(200);
//            connManager.setDefaultMaxPerRoute(20);
//        } catch (KeyManagementException e) {
//            LOGGER.error("KeyManagementException", e);
//        } catch (NoSuchAlgorithmException e) {
//            LOGGER.error("NoSuchAlgorithmException", e);
//        }
//    }
//
//    /**
//     * Get方式提交,URL中包含查询参数, 格式：http://www.g.cn?search=p&name=s.....
//     *
//     * @param url 提交地址
//     * @return 响应消息
//     */
//    public static String doGet(String url) {
//        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet(url);
//        try {
//            httpGet.setHeader(HttpHeaders.CONTENT_ENCODING, CHARSET_UTF8);
//            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
//            int statusCode = httpResponse.getStatusLine().getStatusCode();
//            if (statusCode == 200) {
//                System.out.println(EntityUtils.toString(httpResponse.getEntity()));
//            }
//        } catch (Exception e) {
//            LOGGER.error("" + e.getMessage());
//        } finally {
//            httpGet.releaseConnection();
//        }
//        return "";
//    }
//
//    /**
//     * Get方式提交,URL中不包含查询参数, 格式：http://www.g.cn
//     *
//     * @param url    提交地址
//     * @param params 查询参数集, 键/值对
//     * @return 响应消息
//     */
//    public static String get(String url, Map<String, Object> params) {
//        return doGet(url, params, null);
//    }
//
//    public static String doGet(String url, Map<String, Object> paramMap, String charset) {
//        return "";
//    }
//
//    public static String doGet(String url, Map<String, Object> paramMap, String charset, int timeout) {
//        return "";
//    }
//
//    /**
//     * Post方式提交,URL中不包含提交参数, 格式：http://www.g.cn
//     *
//     * @param url    提交地址
//     * @param params 提交参数集, 键/值对
//     * @return 响应消息
//     */
//    public static String post(String url, Map<String, Object> params) {
//        return postJsonBody(url, CONNECT_TIMEOUT, params, CHARSET_UTF8);
//    }
//
//    @SuppressWarnings("deprecation")
//    public static String doGet(String url, Map<String, String> params, String encode, int connectTimeout,
//                               int soTimeout) {
//        String responseString = null;
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setSocketTimeout(connectTimeout)
//                .setConnectTimeout(connectTimeout)
//                .setConnectionRequestTimeout(connectTimeout).build();
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(url);
//        int i = 0;
//        for (Map.Entry<String, String> entry : params.entrySet()) {
//            if (i == 0 && !url.contains("?")) {
//                sb.append("?");
//            } else {
//                sb.append("&");
//            }
//            sb.append(entry.getKey());
//            sb.append("=");
//            String value = entry.getValue();
//            try {
//                sb.append(URLEncoder.encode(value, "UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                LOGGER.warn("encode http get params error, value is " + value, e);
//                sb.append(URLEncoder.encode(value));
//            }
//            i++;
//        }
//        LOGGER.info("[HttpUtils Get] begin invoke:" + sb.toString());
//        HttpGet get = new HttpGet(sb.toString());
//        get.setConfig(requestConfig);
//
//        try {
//            CloseableHttpResponse response = httpclient.execute(get);
//            try {
//                HttpEntity entity = response.getEntity();
//                try {
//                    if (entity != null) {
//                        responseString = EntityUtils.toString(entity, encode);
//                    }
//                } finally {
//                    if (entity != null) {
//                        entity.getContent().close();
//                    }
//                }
//            } catch (Exception e) {
//                LOGGER.error(String.format("[HttpUtils Get]get response error, url:%s", sb.toString()), e);
//                return responseString;
//            } finally {
//                if (response != null) {
//                    response.close();
//                }
//            }
//            LOGGER.info(String.format("[HttpUtils Get]Debug url:%s , response string %s:", sb.toString(), responseString));
//        } catch (SocketTimeoutException e) {
//            LOGGER.error(String.format("[HttpUtils Get]invoke get timout error, url:%s", sb.toString()), e);
//            return responseString;
//        } catch (Exception e) {
//            LOGGER.error(String.format("[HttpUtils Get]invoke get error, url:%s", sb.toString()), e);
//        } finally {
//            get.releaseConnection();
//        }
//        return responseString;
//    }
//
//    public final static int CONNECT_TIMEOUT = 5000;
//    public final static int SOCKET_TIMEOUT = 5000;
//
//
//    /**
//     * HTTPS请求，默认超时为5S
//     *
//     * @param reqURL
//     * @param params
//     * @return
//     */
//    public static String connectPostHttps(String reqURL, Map<String, String> params) {
//
//        String responseContent = null;
//
//        HttpPost httpPost = new HttpPost(reqURL);
//        try {
//            RequestConfig requestConfig = RequestConfig.custom()
//                    .setSocketTimeout(SOCKET_TIMEOUT)
//                    .setConnectTimeout(CONNECT_TIMEOUT)
//                    .setConnectionRequestTimeout(CONNECT_TIMEOUT).build();
//
//            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
//            httpPost.setEntity(new UrlEncodedFormEntity(formParams, Consts.UTF_8));
//            httpPost.setConfig(requestConfig);
//            // 绑定到请求 Entry
//            for (Map.Entry<String, String> entry : params.entrySet()) {
//                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//            }
//            CloseableHttpResponse response = httpclient.execute(httpPost);
//            try {
//                // 执行POST请求
//                HttpEntity entity = response.getEntity(); // 获取响应实体
//                try {
//                    if (null != entity) {
//                        responseContent = EntityUtils.toString(entity, Consts.UTF_8);
//                    }
//                } finally {
//                    if (entity != null) {
//                        entity.getContent().close();
//                    }
//                }
//            } finally {
//                if (response != null) {
//                    response.close();
//                }
//            }
//            LOGGER.info("requestURI : " + httpPost.getURI() + ", responseContent: " + responseContent);
//        } catch (ClientProtocolException e) {
//            LOGGER.error("ClientProtocolException", e);
//        } catch (IOException e) {
//            LOGGER.error("IOException", e);
//        } finally {
//            httpPost.releaseConnection();
//        }
//        return responseContent;
//
//    }
//
//    public static String postJsonBody(String url, int timeout, Map<String, Object> map, String encoding) {
//        HttpPost post = new HttpPost(url);
//        try {
//            post.setHeader("Content-type", "application/json");
//            RequestConfig requestConfig = RequestConfig.custom()
//                    .setSocketTimeout(timeout)
//                    .setConnectTimeout(timeout)
//                    .setConnectionRequestTimeout(timeout)
//                    .setExpectContinueEnabled(false).build();
//            post.setConfig(requestConfig);
//
//            String str1 = JSON.toJSONString(map).replace("\\", "");
//            post.setEntity(new StringEntity(str1, encoding));
//            LOGGER.info("[HttpUtils Post] begin invoke url:" + url + " , params:" + str1);
//            CloseableHttpResponse response = httpclient.execute(post);
//            try {
//                HttpEntity entity = response.getEntity();
//                try {
//                    if (entity != null) {
//                        String str = EntityUtils.toString(entity, encoding);
//                        LOGGER.info("[HttpUtils Post]Debug response, url :" + url + " , response string :" + str);
//                        return str;
//                    }
//                } finally {
//                    if (entity != null) {
//                        entity.getContent().close();
//                    }
//                }
//            } finally {
//                if (response != null) {
//                    response.close();
//                }
//            }
//        } catch (UnsupportedEncodingException e) {
//            LOGGER.error("UnsupportedEncodingException", e);
//        } catch (Exception e) {
//            LOGGER.error("Exception", e);
//        } finally {
//            post.releaseConnection();
//        }
//        return "";
//    }
//
//    /**
//     * 将传入的键/值对参数转换为NameValuePair参数集
//     *
//     * @param paramsMap 参数集, 键/值对
//     * @return NameValuePair参数集
//     */
//    private static List<NameValuePair> getParamsList(Map<String, String> paramsMap) {
//        if (paramsMap == null || paramsMap.size() == 0) {
//            return null;
//        }
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        for (Map.Entry<String, String> map : paramsMap.entrySet()) {
//            params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
//        }
//        return params;
//    }
//
//    /**
//     *
//     * @param response
//     * @param header    HttpHeaders.ACCEPT
//     * @param value
//     * @return
//     */
//    public static HttpResponse addHeader(HttpResponse response, String header, String value) {
//        if (response!= null) {
//            response.addHeader(header, value);
//        }
//        return response;
//    }
//
//    public static String doGetTest() {
//
//        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
//
//        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
////        HttpGet httpGet = new HttpGet("http://www.gxnu.edu.cn/default.html");
//        HttpGet httpGet = new HttpGet("http://www.renren.com");
//        System.out.println(httpGet.getRequestLine());
//        try {
//            //执行get请求
//            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
//            //获取响应消息实体
//            HttpEntity entity = httpResponse.getEntity();
//            //响应状态
//            System.out.println("status:" + httpResponse.getStatusLine());
//            //判断响应实体是否为空
//            if (entity != null) {
//                System.out.println("contentEncoding:" + entity.getContentEncoding());
//                System.out.println("response content:" + EntityUtils.toString(entity));
//            }
//        } catch (IOException e) {
//            System.out.println("" + e.getMessage());
//        } finally {
//            try {
//                //关闭流并释放资源
//                closeableHttpClient.close();
//            } catch (IOException e) {
//                System.out.println(e.getMessage());
//            }
//        }
//        return "";
//    }
//
//    public static void test() {
//        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet("");
//        CloseableHttpResponse response = null;
//        try {
//            response = closeableHttpClient.execute(httpGet);
//        } catch (IOException e) {
//            System.out.println("" + e.getMessage());
//        } finally {
//            try {
//                response.close();
//            } catch (IOException e) {
//                System.out.println("" + e.getMessage());
//            }
//        }
//    }
//
//    public static void main(final String[] args) {
//        Map<String, Object> paramMap = new HashMap<String, Object>();
//        paramMap.put("cmd", "get_score");
//        paramMap.put("user_id", "773476");
//        paramMap.put("field", "work");
////        final String result = HttpClientUtilTest.get(js);
////        final String result = HttpClientUtilTest.get("http://10.3.17.30:9527", paramMap);
////        final String x = HttpClientUtilTest.doPost("http://10.3.17.30:9527",paramMap);
////        final String x = doPost("10.3.17.30:9527?cmd=get_score&user_id=11776377&field=work", new HashMap<String,
////                String>());
////        System.out.println(result);
////        HttpClientUtilTest.post("http://www.gxnu.edu.cn/default.html", paramMap);
//        String url = "http://test.zhichang.renren.com/campus2015/result?city=1101";
//        HttpClientUtilTest.doGet(url);
////        test();
//
//    }
//}
