package com.meki.play.util.config.loader;

import com.meki.play.util.config.Configuration;

import java.util.List;

/**
 * User: jinchao.xu
 * Date: 14-10-10
 * Time: 下午4:20
 */
public interface PropertiesLoader<T extends Configuration> {
    List<T> loadPropertiesConfiguration();
}
