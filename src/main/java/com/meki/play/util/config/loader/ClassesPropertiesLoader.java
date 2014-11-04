package com.meki.play.util.config.loader;

import com.meki.play.util.config.filter.ConfigResourceFilter;
import com.meki.play.util.config.impl.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * User: jinchao.xu
 * Date: 14-10-10
 * Time: 下午4:22
 */
public class ClassesPropertiesLoader implements PropertiesLoader<PropertiesConfiguration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassesPropertiesLoader.class);

    private final ConfigResourceFilter<File> fileFilter;

    public ClassesPropertiesLoader(ConfigResourceFilter<File> fileFilter){
        this.fileFilter = fileFilter;
    }

    @Override
    public List<PropertiesConfiguration> loadPropertiesConfiguration() {
        List<PropertiesConfiguration> configurations = new ArrayList<PropertiesConfiguration>();
        Set<File> classesPath = getLoadClassesPath();
        for (File dirRoot : classesPath) {
            LOGGER.info("scan {} for properties file.", dirRoot.getAbsolutePath());
            FileFilter filter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return fileFilter.checkResource(pathname);
                }
            };
            for (File file: dirRoot.listFiles(filter)){
                PropertiesConfiguration configuration = new PropertiesConfiguration(file.getName());
                configurations.add(configuration);
                LOGGER.info("load from {}, config:{}", file.getName(), configuration);
            }
        }
        return configurations;
    }

    /**
     * load configuration file from class path
     * default root path  is classes dir
     * @return
     */
    private Set<File> getLoadClassesPath() {
        Map<String, File> classDirMap = new HashMap<String, File>();
        ClassLoader classLoader = getClassLoader();
        Enumeration<URL> dirUrls = null;
        try {
            dirUrls = classLoader.getResources("");
            while (dirUrls.hasMoreElements()) {
                URL classesRoot = (URL) dirUrls.nextElement();
                if (classesRoot != null) {
                    String dirFileName = classesRoot.getFile();
                    File dirRoot = new File(dirFileName);
                    if (dirRoot.exists() && dirRoot.isDirectory()) {
                        classDirMap.put(dirRoot.getAbsolutePath(), dirRoot);
                    } else {
                        LOGGER.error("{} is not exists or is a file.", dirFileName);
                    }
                } else {
                    LOGGER.error("can not find classpath");
                }
            }
        } catch (IOException e) {
            LOGGER.error("load from classes path error,e={}", e);
        }
        return new HashSet(classDirMap.values());
    }

    private ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        return classLoader;
    }
}
