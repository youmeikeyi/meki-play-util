/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util.download;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;

/**
 * 网页内容处理类
 *
 * @param <T>
 *
 * @author Administrator
 */
public abstract class PageContentProcessService<T> {

    private NodeFilter nodeFilter;

    public NodeFilter getNodeFilter() {
        return nodeFilter;
    }

    public void setNodeFilter(NodeFilter nodeFilter) {
        this.nodeFilter = nodeFilter;
    }

    abstract void apply(T output, Node input);

    abstract T getOuput();
}
