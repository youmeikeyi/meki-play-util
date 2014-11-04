package com.meki.play.util.config.impl;

import java.util.Map;

/**
 * User: jinchao.xu
 * Date: 14-10-10
 * Time: 下午3:12
 */
public class MapConfiguration extends AbstractConfiguration {
    private final Map<String, String> config;

    public MapConfiguration(Map<String, String> config){
        this.config = config;
    }

    @Override
    protected Map<String, String> getConfigData() {
        return this.config;
    }
}
