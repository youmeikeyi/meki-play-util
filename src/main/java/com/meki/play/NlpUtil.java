/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * NLPC接口工具类
 * Created by xujinchao on 18/4/13.
 */
public class NlpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(NlpUtil.class);

    static Splitter OMIT_EMPTY_SPLIT = Splitter.on(" ").omitEmptyStrings().trimResults();

    private static final String NLPC_NERL_PLUS =
            "http://bj01.nlpc.baidu.com/nlpc_nerl_plus_121?username=xujinchao&app=nlpc_201804031440279092"
                    + "&access_token=aa4726359ed5fbbb737fdeb4d48a4294&encoding=utf8";

    private static final String NLPC_LEXTAG_NEW =
            "http://bj01.nlpc.baidu.com/nlpc_lextag_110?username=xujinchao&app=nlpc_201803221052418950&expiredate"
                    + "=1533209346&access_token=8d8314d81f33fe03f9685aae9a6c93ed&encoding=utf8";

    /**
     * 词性枚举
     */
    private enum NlpcPosEnum {

        GENERAL_NOUNS("n", "普通名词"),
        //        f    方位名词
        //        s    处所名词
        //        t    时间名词
        NAME("nr", "人名"),
        PLACE_NAME("ns", "地名"),
        //        nt    机构团体名
        //        nw    作品名
        //        nz    其他专名
        //        v    普通动词
        //        vd    动副词
        //        vn    名动词
        ADJECTIVES("a", "形容词"),
        //        ad    副形词
        ADJECTIVES_NOUNS("an", "名形词"),
        //        d    副词
        NUM_QUANTIFER("m", "数量词"),
        QUANTIFER("q", "量词");
        private String code;
        private String desc;

        NlpcPosEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    /**
     * 利用百度nlp从字符串中提取结果
     *
     * @param input 待提取文本串
     *
     * @return 百度nlp返回结果
     */
    private static String getNlpcResult(String input, String url) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", input);

//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(input);
//        jsonObject.put("texts", jsonArray);


        String result = getByPostJsonParams(url, jsonObject);

        return result;
//                getNlpcJsonByCurl("-d " + jsonObject, url);
    }

    /**
     * 请求内容为json格式的post请求
     * @param url   请求链接
     * @param requestJson   请求内容(json格式)
     * @return  post请求返回字符串
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


    /**
     * 从网页文本中提取命名实体的集合
     * 目的是提取合法公司名,加了长度判断
     *
     * @param text 网页文本
     * @param type 提取类型
     *
     * @return 提取结果
     */
    public static String getNlpcLexTagNew(String text, int type) {

        String nplcResult = getNlpcResult(text, AuthService.getFullRequest());

        List<String> result = Lists.newArrayList();
        List<String> posList = Lists.newArrayList();
        switch (type) {
            case 1:
                // 动物识别
                posList.add(NlpcPosEnum.GENERAL_NOUNS.getCode());
                break;
            case 2:
                // calculate
                posList.add(NlpcPosEnum.NUM_QUANTIFER.getCode());
                //                result.addAll(extractFromNplcResult(nplcResult, posList));
                break;
            case 3:
                // 颜色
                posList.add(NlpcPosEnum.ADJECTIVES_NOUNS.getCode());

                posList.add(NlpcPosEnum.GENERAL_NOUNS.getCode());
                //                result.addAll(extractFromNplcResult(nplcResult, posList));
                break;
            default:
                posList.add(NlpcPosEnum.GENERAL_NOUNS.getCode());
        }
        result.addAll(extractFromNplcResult(nplcResult, posList));

        // 默认返回最后一个
        return CollectionUtils.isEmpty(result) ? StringUtils.EMPTY : result.get(result.size() - 1);
    }

    /**
     * 从文本中提取量词
     *
     * @param aipResult 待提取文本
     * @param pos        pos值  m数字 n名词 a形容词等集合
     *
     * @return 提取出的命名实体结果
     */
    public static List<String> extractFromAipResult(String aipResult, List<String> pos) {
        List<String> targetList = Lists.newArrayList();

        try {
            if (StringUtils.isEmpty(aipResult)) {
                return targetList;
            }
            LOG.info("aip:{}", aipResult);
            JSONObject jsonObject = JSON.parseObject(aipResult);

            JSONArray itemsArrayJson = jsonObject.getJSONArray("items");

            if (itemsArrayJson == null) {
                return targetList;
            }

            int itemSize = itemsArrayJson.size();

            for (int itemIndex = 0; itemIndex < itemSize; itemIndex++) {
                JSONObject itemJson = itemsArrayJson.getJSONObject(itemIndex);
                String posStr = itemJson.getString("pos");
                if (StringUtils.isNotEmpty(posStr)
                        && pos.contains(posStr)) {

                    String itemValue = itemJson.getString("item");
                    targetList.add(itemValue);

                }
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return targetList;
    }

    /**
     * 从文本中提取量词
     *
     * @param curlResult 待提取文本
     * @param pos        pos值  m数字 n名词 a形容词等集合
     *
     * @return 提取出的命名实体结果
     */
    public static List<String> extractFromNplcResult(String curlResult, List<String> pos) {
        List<String> targetList = Lists.newArrayList();

        try {
            if (StringUtils.isEmpty(curlResult)) {
                return targetList;
            }
            LOG.info("curl:{}", curlResult);
            JSONObject jsonObject = JSON.parseObject(curlResult);
            if (jsonObject.getJSONArray("results") == null) {
                return targetList;
            }

            JSONArray results = jsonObject.getJSONArray("results");

            for (int index = 0; index < results.size(); index++) {
                JSONObject itemsJson = results.getJSONObject(index);
                if (itemsJson.getInteger("retcode") == 1) {
                    continue;
                }
                if (itemsJson.getJSONArray("items") == null) {
                    continue;
                }
                JSONArray itemsArrayJson = itemsJson.getJSONArray("items");
                for (int itemIndex = 0; itemIndex < itemsArrayJson.size(); itemIndex++) {
                    JSONObject itemJson = itemsArrayJson.getJSONObject(itemIndex);
                    String posStr = itemJson.getString("pos");
                    if (StringUtils.isNotEmpty(posStr)
                            && pos.contains(posStr)) {

                        String itemValue = itemJson.getString("item");
//                        if (NumberUtils.isNumber(itemValue)) {
                        targetList.add(itemValue);
//                        }

                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return targetList;
    }

    /**
     * 利用curl获取指定链接的NLP结果
     *
     * @param type 参数类型
     * @param url  指定链接
     *
     * @return 返回json结果
     */
    private static String getNlpcJsonByCurl(String type, String url) {
        String[] cmds = {"curl", type, url};
        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
        processBuilder.redirectErrorStream(true);
        Process process;

        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = null;
        String line = null;
        try {
            process = processBuilder.start();

            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), Consts.UTF_8));
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String result = builder.toString();
        int index = result.indexOf("{");
        if (index > -1) {
            return result.substring(index);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 从文本中提取命名实体
     *
     * @param curlResult 待提取文本
     *
     * @return 提取出的命名实体结果
     */
    public static Set<String> extractNamedEntity(String curlResult) {
        Set<String> namedEntities = Sets.newHashSet();

        try {
            if (StringUtils.isEmpty(curlResult)) {
                return namedEntities;
            }
            JSONObject jsonObject = JSON.parseObject(curlResult);
            if (jsonObject.getJSONArray("results") == null) {
                return namedEntities;
            }

            JSONArray results = jsonObject.getJSONArray("results");

            for (int index = 0; index < results.size(); index++) {
                JSONObject itemsJson = results.getJSONObject(index);
                if (itemsJson.getInteger("retcode") == 1) {
                    continue;
                }
                if (itemsJson.getJSONArray("items") == null) {
                    continue;
                }
                JSONArray itemsArrayJson = itemsJson.getJSONArray("items");
                for (int itemIndex = 0; itemIndex < itemsArrayJson.size(); itemIndex++) {
                    JSONObject itemJson = itemsArrayJson.getJSONObject(itemIndex);
                    String neValue = itemJson.getString("ne");
                    if (StringUtils.isNotEmpty(neValue)
                            && neValue.startsWith("12")) {
                        if (StringUtils.length(itemJson.getString("item")) < 8) {
                            continue;
                        }
                        namedEntities.add(itemJson.getString("item"));
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return namedEntities;
    }

    public static void main(String[] args) {
        String text = "是小老虎啊";
        String result = getNlpcLexTagNew(text, 1);

        LOG.info("", result);
        LOG.info("END");
    }
}
