/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play;

import com.alibaba.fastjson.JSON;

/**
 * 
 * Json Result Util
 * 
 * @author wuliang07
 *
 */

public class JsonResultUtils {

    private static final int SUCCESS_CODE = 0;

    private static final int FAIL_CODE = 1;

    private static final String SUCCESS =
            JSON.toJSONString(new JsonResult(SUCCESS_CODE, "success", "", ""));

    private static final String PARAM_FAILURE =
            JSON.toJSONString(new JsonResult(FAIL_CODE, "failed", "参数错误", ""));

    private static final String SYSTEM_FAILURE =
            JSON.toJSONString(new JsonResult(FAIL_CODE, "failed", "系统错误", ""));

    private static final String COMPANY_EMPTY =
            JSON.toJSONString(new JsonResult(FAIL_CODE, "failed", "广告主名称为必填项", ""));

    public static String getParamFailedJsonResult() {
        return PARAM_FAILURE;
    }

    public static String getCompanyEmptyJsonResult() {
        return COMPANY_EMPTY;
    }

    public static String getSystemFailedJsonResult() {
        return SYSTEM_FAILURE;
    }

    public static String getFailedJsonResult(String msg) {
        JsonResult jsonResult = new JsonResult(FAIL_CODE, "failed", msg, "");
        return JSON.toJSONString(jsonResult);

    }

    public static String getSuccessJsonResult() {
        return SUCCESS;
    }

    public static String getSuccessJsonResult(String data) {
        JsonResult jsonResult = new JsonResult(SUCCESS_CODE, "success", "", data);
        return JSON.toJSONString(jsonResult);

    }

    public static String getSuccessJsonResult(Object obj) {
        JsonResult jsonResult = new JsonResult(SUCCESS_CODE, "success", "", obj);
        return JSON.toJSONString(jsonResult);
    }

    public static String getSuccessJsonResult(Object obj, boolean forceObjStr) {
        if (forceObjStr) {
            String data = JSON.toJSONString(obj);
            return getSuccessJsonResult(data);
        }
        return getSuccessJsonResult(obj);
    }


    public static String reder2Json(Object o, Class<?> cls) {
        JsonResult jsonResult = new JsonResult(SUCCESS_CODE, "success", "", o);
        return JSON.toJSONString(jsonResult);

    }

}
