/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util.download;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Http Request实体
 *
 * @author Administrator
 */
public class HttpRequestInfo {

    // legal url
    private List<String> legalUrls;
    // illegal url
    private List<String> illegalUrls;
    // url req/rep cost milisec
    private ConcurrentHashMap<String, Long> cost;
    // illegal return code
    private ConcurrentHashMap<String, String> illegalCode;

    // url store path
    private ConcurrentHashMap<String, String> storePath;

    public List<String> getLegalUrls() {
        return legalUrls;
    }

    public void setLegalUrls(List<String> legalUrls) {
        this.legalUrls = legalUrls;
    }

    public List<String> getIllegalUrls() {
        return illegalUrls;
    }

    public void setIllegalUrls(List<String> illegalUrls) {
        this.illegalUrls = illegalUrls;
    }

    public ConcurrentHashMap<String, Long> getCost() {
        return cost;
    }

    public void setCost(ConcurrentHashMap<String, Long> cost) {
        this.cost = cost;
    }

    public ConcurrentHashMap<String, String> getIllegalCode() {
        return illegalCode;
    }

    public void setIllegalCode(ConcurrentHashMap<String, String> illegalCode) {
        this.illegalCode = illegalCode;
    }

    public ConcurrentHashMap<String, String> getStorePath() {
        return storePath;
    }

    public void setStorePath(ConcurrentHashMap<String, String> storePath) {
        this.storePath = storePath;
    }
}
