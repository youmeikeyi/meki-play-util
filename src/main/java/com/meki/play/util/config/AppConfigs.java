package com.meki.play.util.config;

import java.util.Map;

/**
 * User: jinchao.xu
 * Date: 14-10-10
 * Time: 下午2:53
 */
public final class AppConfigs {
    private static final ConfigurationManager CONFIGURATION_MANAGER = ConfigurationManager.getInstance();
    private static AppConfigs instance = new AppConfigs();

    private AppConfigs(){}

    public static AppConfigs getInstance(){
        return instance;
    }

    public Map<String, String> getConfigs(){
        return CONFIGURATION_MANAGER.toMap();
    }

    public String get(String key) {
        return CONFIGURATION_MANAGER.getString(key);
    }

    public Integer getInt(String key) {
        return CONFIGURATION_MANAGER.getInt(key);
    }

    public String getByDefaultStr(String key, String defaultStr) {
        String result = get(key);
        if (result == null) {
            result = defaultStr;
        }
        return result;
    }
}
