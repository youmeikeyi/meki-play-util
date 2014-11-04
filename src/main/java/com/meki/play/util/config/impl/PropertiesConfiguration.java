package com.meki.play.util.config.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

/**
 * User: jinchao.xu
 * Date: 14-10-10
 * Time: 下午3:15
 */
public class PropertiesConfiguration extends AbstractConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesConfiguration.class);

    private final String configName;
    private Map<String, String> configData;

    public PropertiesConfiguration(String configName) {
        this.configName = configName;
        this.configData = buildConfigData();
    }

    /**
     * build confi data by class loader through current thread
     * @return
     */
    private Map<String, String> buildConfigData() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        return loadFromClassLoader(classLoader);
    }

    /**
     * ClassLoader load config file from the specified configName
     * @param classLoader
     * @return
     */
    private Map<String, String> loadFromClassLoader(ClassLoader classLoader) {
        if (classLoader != null) {
            try {
                URL url = classLoader.getResource(this.configName);
                return readProperties(url);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return Collections.emptyMap();
    }

    public String getConfigName() {
        return configName;
    }

    @Override
    protected Map<String, String> getConfigData() {
        return configData;
    }
}
