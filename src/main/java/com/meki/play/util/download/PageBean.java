/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util.download;

import com.baidu.rigel.feed.common.enums.ParseResultTypeEnum;

import java.util.Date;

/**
 * http请求返回内容封装
 */
public class PageBean {

    private String url;

    // 经过处理的真实解析url
    private String parseUrl;

    private String html;

    private String charSet;

    private Integer httpStatus;

    // exception
    private String reason;
    // msg
    private String msg;

    private ParseResultTypeEnum resultTypeEnum;

    private ParseResultTypeEnum detailTypeEnum;

    private Byte adPlt;

    private String adSource;
    //
    private String parseEncryption;

    // 页面文本
    private String pageContent;

    // 解析文本内容
    private String content;

    private Date fetchTime;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ParseResultTypeEnum getResultTypeEnum() {
        return resultTypeEnum;
    }

    public void setResultTypeEnum(ParseResultTypeEnum resultTypeEnum) {
        this.resultTypeEnum = resultTypeEnum;
    }

    public String getParseEncryption() {
        return parseEncryption;
    }

    public void setParseEncryption(String parseEncryption) {
        this.parseEncryption = parseEncryption;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParseUrl() {
        return parseUrl;
    }

    public void setParseUrl(String parseUrl) {
        this.parseUrl = parseUrl;
    }

    public ParseResultTypeEnum getDetailTypeEnum() {
        return detailTypeEnum;
    }

    public void setDetailTypeEnum(ParseResultTypeEnum detailTypeEnum) {
        this.detailTypeEnum = detailTypeEnum;
    }

    public Byte getAdPlt() {
        return adPlt;
    }

    public void setAdPlt(Byte adPlt) {
        this.adPlt = adPlt;
    }

    public String getAdSource() {
        return adSource;
    }

    public void setAdSource(String adSource) {
        this.adSource = adSource;
    }

    public String getPageContent() {
        return pageContent;
    }

    public void setPageContent(String pageContent) {
        this.pageContent = pageContent;
    }

    public Date getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(Date fetchTime) {
        this.fetchTime = fetchTime;
    }
}