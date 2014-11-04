package com.meki.play.util.config;

import com.meki.play.util.config.constant.ConfigConstant;
import com.meki.play.util.config.filter.ConfigResourceFilter;
import com.meki.play.util.config.impl.AbstractConfiguration;
import com.meki.play.util.config.impl.MapConfiguration;
import com.meki.play.util.config.impl.PropertiesConfiguration;
import com.meki.play.util.config.loader.ClassesPropertiesLoader;
import com.meki.play.util.config.loader.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置文件管理器
 * User: jinchao.xu
 * Date: 14-10-10
 * Time: 下午2:53
 */
public class ConfigurationManager extends AbstractConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManager.class);

    private static ConfigurationManager instance = new ConfigurationManager();

    //the configuration load from classes dir , key is file name ,value is configuration
    private final Map<String, PropertiesConfiguration> classesConfigurationMap = Collections.synchronizedMap(new HashMap<String, PropertiesConfiguration>());
    private final PropertiesLoader<PropertiesConfiguration> classesPropertiesLoader = new ClassesPropertiesLoader(new ClassResourceFilter());

    private Configuration classesConfiguration;

    private ConfigurationManager(){
        initClassesConfigurationMap();
        initClassesConfiguration();
    }

    public static ConfigurationManager getInstance() {
        return instance;
    }

    private void initClassesConfigurationMap() {
        List<PropertiesConfiguration> classesConfigurations = classesPropertiesLoader.loadPropertiesConfiguration();
        LOGGER.info("load from class path.");
        for (PropertiesConfiguration configuration : classesConfigurations) {
            if (classesConfigurationMap.containsKey(configuration.getConfigName())) {
                LOGGER.error("in the class path find the name {}.", configuration.getConfigName());
                throw new RuntimeException(String.format("in the class path find the name %s.", configuration.getConfigName()));
            } else {
                classesConfigurationMap.put(configuration.getConfigName(), configuration);
            }
        }
        LOGGER.info("init class path configuration success, key size:{}.", classesConfigurationMap.size());
    }

    private void initClassesConfiguration() {
        LOGGER.info("init class path configuration.");
        Map<String, String> margeMap = new HashMap<String, String>();
        for (PropertiesConfiguration configuration : this.classesConfigurationMap.values()) {
            for (Map.Entry<String, String> e : configuration.toMap().entrySet()) {
                if (margeMap.containsKey(e.getKey())) {
                    throw new RuntimeException("class path properties have same key " + e.getKey() + " in " + configuration.getConfigName());
                } else {
                    margeMap.put(e.getKey(), e.getValue());
                }
            }
        }
        LOGGER.info("class path configuration have key size:{}.", margeMap.size());
        this.classesConfiguration = new MapConfiguration(margeMap);
    }

    @Override
    protected Map<String, String> getConfigData() {
        return this.classesConfiguration.toMap();
    }

    private static class ClassResourceFilter implements ConfigResourceFilter<File> {
        @Override
        public boolean checkResource(File resource) {
            return resource.getName().startsWith(ConfigConstant.MEKI_CONFIG_PREFIX)
                    && resource.getName().endsWith(ConfigConstant.MEKI_CONFIG_SUFFIX);
        }
    }
}
