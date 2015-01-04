package com.meki.play.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.*;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: jinchao.xu
 * Date: 2014/8/12
 * Time: 16:32
 */
public class HttpClientUtilTest {
    private static Logger LOGGER = LoggerFactory.getLogger(HttpClientUtilTest.class);

    private static final String CHARSET_UTF8 = "UTF-8";
    private static final String CHARSET_GBK = "GBK";
    private static final String SSL_DEFAULT_SCHEME = "https";
    private static final int SSL_DEFAULT_PORT = 443;

    private static PoolingHttpClientConnectionManager connManager = null;
    private static CloseableHttpClient httpclient = null;

    static {
        try {
            SSLContext sslContext = SSLContexts.custom().useTLS().build();
            sslContext.init(null,
                    new TrustManager[]{new X509TrustManager() {

                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(
                                X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                X509Certificate[] certs, String authType) {
                        }
                    }}, null);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext))
                    .build();

            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            httpclient = HttpClients.custom().setConnectionManager(connManager).build();
            // Create socket configuration
            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);
            // Create message constraints
            MessageConstraints messageConstraints = MessageConstraints.custom()
                    .setMaxHeaderCount(200)
                    .setMaxLineLength(2000)
                    .build();
            // Create connection configuration
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE)
                    .setCharset(Consts.UTF_8)
                    .setMessageConstraints(messageConstraints)
                    .build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(200);
            connManager.setDefaultMaxPerRoute(20);
        } catch (KeyManagementException e) {
            LOGGER.error("KeyManagementException", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("NoSuchAlgorithmException", e);
        }
    }

    /**
     * Get方式提交,URL中包含查询参数, 格式：http://www.g.cn?search=p&name=s.....
     *
     * @param url 提交地址
     * @return 响应消息
     */
    public static String doGet(String url) {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            httpGet.setHeader(HttpHeaders.CONTENT_ENCODING, CHARSET_UTF8);
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                System.out.println(EntityUtils.toString(httpResponse.getEntity()));
                return EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (Exception e) {
            LOGGER.error("" + e.getMessage());
        } finally {
            httpGet.releaseConnection();
        }
        return "";
    }

    /**
     * Get方式提交,URL中不包含查询参数, 格式：http://www.g.cn
     *
     * @param url    提交地址
     * @param params 查询参数集, 键/值对
     * @return 响应消息
     */
    public static String get(String url, Map<String, Object> params) {
        return doGet(url, params, null);
    }

    public static String doGet(String url, Map<String, Object> paramMap, String charset) {
        return "";
    }

    public static String doGet(String url, Map<String, Object> paramMap, String charset, int timeout) {
        return "";
    }

    /**
     * Post方式提交,URL中不包含提交参数, 格式：http://www.g.cn
     *
     * @param url    提交地址
     * @param params 提交参数集, 键/值对
     * @return 响应消息
     */
    public static String post(String url, Map<String, Object> params) {
        return postJsonBody(url, CONNECT_TIMEOUT, params, CHARSET_UTF8);
    }

    @SuppressWarnings("deprecation")
    public static String doGet(String url, Map<String, String> params, String encode, int connectTimeout,
                               int soTimeout) {
        String responseString = null;
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(connectTimeout)
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectTimeout).build();

        StringBuilder sb = new StringBuilder();
        sb.append(url);
        int i = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (i == 0 && !url.contains("?")) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            sb.append(entry.getKey());
            sb.append("=");
            String value = entry.getValue();
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                LOGGER.warn("encode http get params error, value is " + value, e);
                sb.append(URLEncoder.encode(value));
            }
            i++;
        }
        LOGGER.info("[HttpUtils Get] begin invoke:" + sb.toString());
        HttpGet get = new HttpGet(sb.toString());
        get.setConfig(requestConfig);

        try {
            CloseableHttpResponse response = httpclient.execute(get);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if (entity != null) {
                        responseString = EntityUtils.toString(entity, encode);
                    }
                } finally {
                    if (entity != null) {
                        entity.getContent().close();
                    }
                }
            } catch (Exception e) {
                LOGGER.error(String.format("[HttpUtils Get]get response error, url:%s", sb.toString()), e);
                return responseString;
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            LOGGER.info(String.format("[HttpUtils Get]Debug url:%s , response string %s:", sb.toString(), responseString));
        } catch (SocketTimeoutException e) {
            LOGGER.error(String.format("[HttpUtils Get]invoke get timout error, url:%s", sb.toString()), e);
            return responseString;
        } catch (Exception e) {
            LOGGER.error(String.format("[HttpUtils Get]invoke get error, url:%s", sb.toString()), e);
        } finally {
            get.releaseConnection();
        }
        return responseString;
    }

    public final static int CONNECT_TIMEOUT = 5000;
    public final static int SOCKET_TIMEOUT = 5000;


    /**
     * HTTPS请求，默认超时为5S
     *
     * @param reqURL
     * @param params
     * @return
     */
    public static String connectPostHttps(String reqURL, Map<String, String> params) {

        String responseContent = null;

        HttpPost httpPost = new HttpPost(reqURL);
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setConnectionRequestTimeout(CONNECT_TIMEOUT).build();

            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, Consts.UTF_8));
            httpPost.setConfig(requestConfig);
            // 绑定到请求 Entry
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                // 执行POST请求
                HttpEntity entity = response.getEntity(); // 获取响应实体
                try {
                    if (null != entity) {
                        responseContent = EntityUtils.toString(entity, Consts.UTF_8);
                    }
                } finally {
                    if (entity != null) {
                        entity.getContent().close();
                    }
                }
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            LOGGER.info("requestURI : " + httpPost.getURI() + ", responseContent: " + responseContent);
        } catch (ClientProtocolException e) {
            LOGGER.error("ClientProtocolException", e);
        } catch (IOException e) {
            LOGGER.error("IOException", e);
        } finally {
            httpPost.releaseConnection();
        }
        return responseContent;

    }

    public static String postJsonBody(String url, int timeout, Map<String, Object> map, String encoding) {
        HttpPost post = new HttpPost(url);
        try {
            post.setHeader("Content-type", "application/json");
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setExpectContinueEnabled(false).build();
            post.setConfig(requestConfig);

            String str1 = JSON.toJSONString(map).replace("\\", "");
            post.setEntity(new StringEntity(str1, encoding));
            LOGGER.info("[HttpUtils Post] begin invoke url:" + url + " , params:" + str1);
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if (entity != null) {
                        String str = EntityUtils.toString(entity, encoding);
                        LOGGER.info("[HttpUtils Post]Debug response, url :" + url + " , response string :" + str);
                        return str;
                    }
                } finally {
                    if (entity != null) {
                        entity.getContent().close();
                    }
                }
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UnsupportedEncodingException", e);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        } finally {
            post.releaseConnection();
        }
        return "";
    }

    /**
     * 将传入的键/值对参数转换为NameValuePair参数集
     *
     * @param paramsMap 参数集, 键/值对
     * @return NameValuePair参数集
     */
    private static List<NameValuePair> getParamsList(Map<String, String> paramsMap) {
        if (paramsMap == null || paramsMap.size() == 0) {
            return null;
        }
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> map : paramsMap.entrySet()) {
            params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
        }
        return params;
    }

    /**
     * @param response
     * @param header   HttpHeaders.ACCEPT
     * @param value
     * @return
     */
    public static HttpResponse addHeader(HttpResponse response, String header, String value) {
        if (response != null) {
            response.addHeader(header, value);
        }
        return response;
    }

    final static StringBuffer buffer = new StringBuffer();

    public static String doGetTest(String url) {
//        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet("http://www.gxnu.edu.cn/default.html");
        HttpGet httpGet = new HttpGet(url);
        System.out.println(httpGet.getRequestLine());
        try {
            //执行get请求
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
            //获取响应消息实体
            HttpEntity entity = httpResponse.getEntity();
            //响应状态
            System.out.println("status:" + httpResponse.getStatusLine());
            //判断响应实体是否为空
            if (entity != null) {
                System.out.println("contentEncoding:" + entity.getContentEncoding());
                //EntityUtils会消耗掉entity，导致后面无法继续读
//                System.out.println("response content:" + EntityUtils.toString(entity));
//                return EntityUtils.toString(entity);
                String line;
                final BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8"));
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                reader.close();
            }
        } catch (IOException e) {
            System.out.println("" + e.getMessage());
        } finally {
            try {
                //关闭流并释放资源
                closeableHttpClient.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return buffer.toString();
    }

    public static void test() {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("");
        CloseableHttpResponse response = null;
        try {
            response = closeableHttpClient.execute(httpGet);
        } catch (IOException e) {
            System.out.println("" + e.getMessage());
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                System.out.println("" + e.getMessage());
            }
        }
    }

    public static void getScore() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cmd", "get_score");
        paramMap.put("user_id", "773476");
        paramMap.put("field", "work");

        String url = "http://zhichang.renren.com/campus2015/result?city=1101";
        HttpClientUtilTest.doGet(url);
    }

    public static void main(final String[] args) {

        System.out.println(validateIdCard("370828199012262011"));
//        final String result = HttpClientUtilTest.get(js);
//        final String result = HttpClientUtilTest.get("http://10.3.17.30:9527", paramMap);
//        final String x = HttpClientUtilTest.doPost("http://10.3.17.30:9527",paramMap);
//        final String x = doPost("10.3.17.30:9527?cmd=get_score&user_id=11776377&field=work", new HashMap<String,
//                String>());
//        System.out.println(result);
//        HttpClientUtilTest.post("http://www.gxnu.edu.cn/default.html", paramMap);

//        test();

    }
    public static String regIdCard = "^(^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$)|(^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[Xx])$)$";

    public static Pattern IDCARD_PATTERN = Pattern.compile(regIdCard);
    public static int MT[] = new int[100];

    static {
        for (int i = 11; i <= 15; i++) {
            MT[i] = 1;
        }
        for (int i = 21; i <= 23; i++) {
            MT[i] = 1;
        }
        for (int i = 31; i <= 37; i++) {
            MT[i] = 1;
        }
        for (int i = 41; i <= 46; i++) {
            MT[i] = 1;
        }
        for (int i = 50; i <= 54; i++) {
            MT[i] = 1;
        }
        for (int i = 61; i <= 65; i++) {
            MT[i] = 1;
        }
        MT[71] = 1;
        MT[81] = 1;
        MT[82] = 1;
        MT[91] = 1;
    }

    public static boolean validateIdCard(String idCard) {
        try {
            if (idCard == null || idCard.length() == 0) {
                return false;
            }
            int index = Integer.parseInt(idCard.substring(0, 2));
            //地域信息
            if (MT[index] == 0) {
                return false;
            }
            Pattern p = IDCARD_PATTERN;
            if (p.matcher(idCard).matches()) {
                if (idCard.length() == 18) {
                    int[] idCardWi = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7,
                            9, 10, 5, 8, 4, 2 };
                    int[] idCardY = new int[] { 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 };
                    int idCardWiSum = 0;
                    for (int i = 0; i < 17; i++) {
                        idCardWiSum += Integer.parseInt(idCard.substring(i, i + 1))
                                * idCardWi[i];
                    }
                    int idCardMod = idCardWiSum % 11;
                    char idCardLast = idCard.charAt(17);
                    if (idCardMod == 2) {
                        if (idCardLast == 'X' || idCardLast == 'x') {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        if (Integer.parseInt(idCardLast + "") == idCardY[idCardMod]) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            } else {
                return false;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String getUl(String source){
        String regex = "<ul>.*?</ul>";
        String table = "";
        final List<String> list = new ArrayList<String>();
        final Pattern pa = Pattern.compile(regex, Pattern.CANON_EQ);
        final Matcher ma = pa.matcher(source);
        while (ma.find()) {
            list.add(ma.group());
        }
        System.out.println("## size:" + list.size());
        for (int i = 0; i < list.size(); i++) {
            table = table + list.get(i);
        }
        System.out.println("## UL: " + table);
        return outTag(table);
    }

    private static void getTable(final String s) {
        String regex = "<table.*?</table>";
        String table = "";
        final List<String> list = new ArrayList<String>();
        final Pattern pa = Pattern.compile(regex, Pattern.CANON_EQ);
        final Matcher ma = pa.matcher(s);
        while (ma.find()) {
            list.add(ma.group());
        }
        System.out.println("## size:" + list.size());
        for (int i = 0; i < list.size(); i++) {
            table = table + list.get(i);
        }
        System.out.println("## table: " + table);
        String tableData =  outTag(table);
        List<String> resultList = new ArrayList<String>(20);
        String[] data = tableData.split("\\s+");
        for (String temp: data) {
            resultList.add(temp);
//            System.out.println(temp);
        }
        //去掉前面的无关信息
        resultList = resultList.subList(9, resultList.size());
        for (int index = 0; index < resultList.size() ; index ++) {
            if (index % 8 == 0) {
                System.out.println();
            }
            System.out.print(resultList.get(index) + ",");
        }

    }

    /**
     * @param s
     * @return 去掉标记
     */
    private static String outTag(final String s) {
        return s.replaceAll("<.*?>", "");
    }

    /**
     * @param s
     * @return 获得链接
     */
    private static List<String> getLink(final String s) {
        String regex;
        final List<String> list = new ArrayList<String>();
        regex = "<a[^>]*href=(\"([^\"]*)\"|\'([^\']*)\'|([^\\s>]*))[^>]*>(.*?)</a>";
        final Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
        final Matcher ma = pa.matcher(s);
        while (ma.find()) {
            list.add(ma.group());
        }
        return list;
    }

    public static String getHtmlByUrl(String url) {
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpget, responseHandler);
            String str = new String(responseBody.getBytes("UTF-8"), "UTF-8");
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return null;
    }

}
