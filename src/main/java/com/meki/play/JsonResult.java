/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play;

/**
 * 
 * Json Result
 * 
 * @author wuliang07
 *
 */
public class JsonResult {

    private Integer status;

    private String statusInfo;

    private String msg;

    private Object data;

    public JsonResult(Integer status, String statusInfo, String msg, Object data) {
        super();
        this.status = status;
        this.statusInfo = statusInfo;
        this.msg = msg;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "JsonResult [status=" + status + ", statusInfo=" + statusInfo + ", msg=" + msg + ", data="
                + data + "]";
    }

}
