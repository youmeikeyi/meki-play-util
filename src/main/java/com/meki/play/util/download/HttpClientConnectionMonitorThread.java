/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util.download;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.HttpClientConnectionManager;

/**
 * <p>Description: 使用管理器，管理HTTP连接池 无效链接定期清理功能</p>
 *
 * @author
 */
public class HttpClientConnectionMonitorThread extends Thread {

    private final HttpClientConnectionManager connManager;
    private volatile boolean shutdown;

    public HttpClientConnectionMonitorThread(HttpClientConnectionManager connManager) {
        super();
        this.setName("http-connection-monitor");
        this.setDaemon(true);
        this.connManager = connManager;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized(this) {
                    wait(5000);
                    // 关闭过期的链接
                    connManager.closeExpiredConnections();
                    // 选择关闭 空闲30秒的链接
                    connManager.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 方法描述: 停止 管理器 清理无效链接  (该方法当前暂时关闭)
     *
     * @author
     */
    @Deprecated
    public void shutDownMonitor() {
        synchronized(this) {
            shutdown = true;
            notifyAll();
        }
    }

}
