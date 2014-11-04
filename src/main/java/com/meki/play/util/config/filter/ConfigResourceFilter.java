package com.meki.play.util.config.filter;

/**
 * 配置文件过滤
 * User: jinchao.xu
 * Date: 14-10-10
 * Time: 下午4:06
 */
public interface ConfigResourceFilter<T> {
    boolean checkResource(T resource);
}
