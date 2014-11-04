package com.meki.play.util.config.impl;

import com.meki.play.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * User: jinchao.xu
 * Date: 14-10-10
 * Time: 下午2:57
 */
public abstract class AbstractConfiguration implements Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfiguration.class);

    protected abstract Map<String, String> getConfigData();

    @Override
    public Integer getInt(String key) {
        return getString(key) != null ? Integer.parseInt(getString(key)) : null;
    }

    @Override
    public String getString(String key) {
        return getConfigData().get(key);
    }

    @Override
    public Properties toProperties() {
        Properties properties = new Properties();
        for (Map.Entry<String, String> entry : getConfigData().entrySet()) {
            properties.put(entry.getKey(), entry.getValue());
        }
        return properties;
    }

    @Override
    public Map<String, String> toMap() {
        return Collections.unmodifiableMap(getConfigData());
    }

    /**
     * read properties from url source
     * @param url
     * @return
     */
    protected Map<String, String> readProperties(URL url){
        try {
            return loadFromStream(url.openStream());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Collections.emptyMap();
    }

    /**
     * read properties from inputstream source
     * @param fis
     * @return
     */
    protected Map<String, String> loadFromStream(InputStream fis) {
        Map<String, String> config = new HashMap<String, String>();
        try {
            Properties p = new Properties();
            p.load(fis);
            for (Map.Entry<Object, Object> entry : p.entrySet()) {
                if (entry.getKey() != null || entry.getKey() != null) {
                    config.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return config;
    }
}
