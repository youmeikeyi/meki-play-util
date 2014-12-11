package com.meki.play.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* User: jinchao.xu
* Date: 2014/8/13
* Time: 14:01
*/
public class HttpClientUtil {
    private static Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    private static CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

    private static final String CHARSET_UTF8 = "UTF-8";
    private static final String CHARSET_GBK = "GBK";
    public final static int CONNECT_TIMEOUT = 5000;
    public final static int SOCKET_TIMEOUT = 5000;

    /**
     * Get方式提交,URL中包含查询参数, 格式：http://www.g.cn?search=p&name=s.....
     *
     * @param url 提交地址
     * @return 响应消息
     */
    public static String doGet(String url) {
        return doGet(url, null);
    }

    /**
     * Get方式提交,URL中不包含查询参数, 格式：http://www.g.cn
     *
     * @param url    提交地址
     * @param params 查询参数集, 键/值对
     * @return 响应消息
     */
    public static String doGet(String url, Map<String, String> params) {
        if (params == null) {
            return doGet(url, CHARSET_UTF8, CONNECT_TIMEOUT);
        }
        return doGet(url, params, null);
    }

    public static String doGet(String url, Map<String, String> paramMap, String charset) {
        return doGet(url, paramMap, charset, CONNECT_TIMEOUT);
    }

    public static String doGet(String url, Map<String, String> paramMap, int timeout) {
        return doGet(url, paramMap, CHARSET_UTF8, timeout);
    }

    public static String doGet(String url,String charset, int timeout) {
        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectionRequestTimeout(timeout).build();
        httpGet.setConfig(requestConfig);
        try {
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = httpResponse.getEntity();
                return EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (Exception e) {
            LOGGER.error("" + e.getMessage());
            return "";
        } finally {
            httpGet.releaseConnection();
            clearHttpClient();
        }
        return "";
    }

    public static String doGet(String url, Map<String, String> paramMap, String charset, int timeout) {
        HttpGet httpGet = new HttpGet(generateUrl(url, paramMap));
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectionRequestTimeout(timeout).build();
        httpGet.setConfig(requestConfig);
        try {
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = httpResponse.getEntity();
                return EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (Exception e) {
            LOGGER.error("" + e.getMessage());
            return "";
        } finally {
            httpGet.releaseConnection();
            clearHttpClient();
        }
        return "";
    }

    private static String generateUrl(String url, Map<String, String> params) {
//        if (StringUtil.isEmail(url)) {
//            return "";
//        }
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
                sb.append(URLEncoder.encode(value, CHARSET_UTF8));
            } catch (UnsupportedEncodingException e) {
                LOGGER.warn("encode http get params error, value is " + value, e);
                sb.append(URLEncoder.encode(value));
            }
            i++;
        }
        return sb.toString();
    }

    public static String doPost(String url, Map<String, String> params, int timeout) {
        return doPost(url, params, CHARSET_UTF8, timeout);
    }

    /**
     * Post方式提交,URL中不包含提交参数, 格式：http://www.xiaozhao.cn
     *
     * @param url    提交地址
     * @param params 提交参数集, 键/值对
     * @return 响应消息
     */
    public static String doPost(String url, Map<String, String> params) {
        return doPost(url, params, CHARSET_UTF8, CONNECT_TIMEOUT);
    }

    public static String doPost(String url, Map<String, String> paramMap, String charset, int timeout) {
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectionRequestTimeout(timeout).build();

        httpPost.setConfig(requestConfig);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(getParamsList(paramMap), charset));
            HttpResponse httpResponse = closeableHttpClient.execute(httpPost);

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = httpResponse.getEntity();
                return EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (Exception e) {
            LOGGER.error("" + e.getMessage());
            return "";
        } finally {
            httpPost.releaseConnection();
            clearHttpClient();
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

    public static void clearHttpClient() {
        try {
            closeableHttpClient.close();
        } catch (IOException e) {
            LOGGER.error("" + e.getMessage());
        }
    }

    public static void main(String[] args){
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("cmd", "get_score");
        paramMap.put("user_id", "773476");
        paramMap.put("field", "work");

        String xiaozhaoUrl = "http://test.zhichang.renren.com/campus2015/result?keyword=%E6%B5%B7%E5%8D%97&city=1101";
        String scoreUrl = "http://10.3.17.30:9527/?cmd=get_score&user_id=11776377&field=work";
        String postUrl_addfans = "http://test.zhichang.renren.com/company/11785843/baseinfo/addfans";
        String postUrl_cancelfans = "http://test.zhichang.renren.com/company/11785843/baseinfo/addfans";

        try {
//            doGet(xiaozhaoUrl);
            String getResult = doGet(scoreUrl);
            System.out.println("======GET========" + getResult);
            String postResult = doPost(scoreUrl, paramMap, 5);
            System.out.println("======POST========" + postResult);

//            String result = Request.Get(xiaozhaoUrl).execute().returnContent().toString();
//            String result = Request.Post(postUrl_addfans)
//                    .bodyForm(Form.form().add("siteId", "11785842").add("user_id", "773476")
//                            .add("_rtk", "6708bf56").build())
//                    .execute().returnContent().toString();
//            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
