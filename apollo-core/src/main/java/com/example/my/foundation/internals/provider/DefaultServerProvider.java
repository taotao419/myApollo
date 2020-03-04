package com.example.my.foundation.internals.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.example.my.foundation.internals.Utils;
import com.example.my.foundation.spi.provider.Provider;
import com.example.my.foundation.spi.provider.ServerProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 取Env和DataCenter
 * 按如下3种情况顺序取得. 如果上一种情况获取失败,则尝试第二种情况
 * 1. JVM system property
 * 2. OS Environment variable [环境变量]
 * 3. 配置文件 [/opt/settings/server.properties]
 */
public class DefaultServerProvider implements ServerProvider {

    private static final Logger logger = LoggerFactory.getLogger(DefaultServerProvider.class);
    private static final String SERVER_PROPERTIES_LINUX = "/opt/settings/server.properties";
    private static final String SERVER_PROPERTIES_WINDOWS = "C:/opt/settings/server.properties";

    private String m_env;
    private String m_dc;

    private Properties m_serverProperties = new Properties();

    @Override
    public void initialize() {
        try {
            String path = Utils.isOSWindows() ? SERVER_PROPERTIES_WINDOWS : SERVER_PROPERTIES_LINUX;

            File file = new File(path);
            if (file.exists() && file.canRead()) {
                logger.info("Loading {}", file.getAbsolutePath());
                FileInputStream fis = new FileInputStream(file);
                initialize(fis);
                return;
            }

            initialize(null);
        } catch (Throwable ex) {
            logger.error("Initialize DefaultServerProvider failed.", ex);
        }
    }

    @Override
    public void initialize(InputStream in) throws IOException {
        try {
            if (in != null) {
                try {
                    m_serverProperties.load(in);
                } finally {
                    in.close();
                }
            }
            
            initEnvType();
            initDataCenter();
        } catch (Throwable ex) {
            logger.error("Initialize DefaultServerProvider failed.", ex);
        }
    }

    @Override
    public Class<? extends Provider> getType() {
        return ServerProvider.class;
    }

    @Override
    public String getProperty(String name, String defaultValue) {
        if ("env".equalsIgnoreCase(name)) {
            String val = getEnvType();
            return val == null ? defaultValue : val;
        } else if ("dc".equalsIgnoreCase(name)) {
            String val = getDataCenter();
            return val == null ? defaultValue : val;
        } else {
            String val = m_serverProperties.getProperty(name, defaultValue);
            return val == null ? defaultValue : val.trim();
        }
    }

    @Override
    public String getEnvType() {
        return m_env;
    }

    @Override
    public boolean isEnvTypeSet() {
        return m_env != null;
    }

    @Override
    public String getDataCenter() {
        return m_dc;
    }

    @Override
    public boolean isDataCenterSet() {
        return m_dc != null;
    }

    private void initEnvType() {
        // 1. fetch from JVM system property
        m_env = System.getProperty("env");
        if (!Utils.isBlank(m_env)) {
            m_env = m_env.trim();
            logger.info("Environment is set to [{}] by JVM system property 'env'.", m_env);
            return;
        }

        // 2. fetch from OS environment variable
        m_env = System.getenv("ENV");
        if (!Utils.isBlank(m_env)) {
            m_env = m_env.trim();
            logger.info("Environment is set to [{}] by OS env variable 'ENV'.", m_env);
            return;
        }

        // 3. fetch from file "server.properties"
        m_env = m_serverProperties.getProperty("env");
        if (!Utils.isBlank(m_env)) {
            m_env = m_env.trim();
            logger.info("Environment is set to [{}] by property 'env' in file server.properties.", m_env);
            return;
        }

        m_env = null;
        logger.info(
                "Environment is set to null. Because it is not available in either (1) JVM system property 'env', (2) OS env variable 'ENV' nor (3) property 'env' from the properties InputStream.");
    }

    private void initDataCenter() {
        // 1. Try to get environment from JVM system property
        m_dc = System.getProperty("idc");
        if (!Utils.isBlank(m_dc)) {
            m_dc = m_dc.trim();
            logger.info("Data Center is set to [{}] by JVM system property 'idc'.", m_dc);
            return;
        }

        // 2. Try to get idc from OS environment variable
        m_dc = System.getenv("IDC");
        if (!Utils.isBlank(m_dc)) {
            m_dc = m_dc.trim();
            logger.info("Data Center is set to [{}] by OS env variable 'IDC'.", m_dc);
            return;
        }

        // 3. Try to get idc from from file "server.properties"
        m_dc = m_serverProperties.getProperty("idc");
        if (!Utils.isBlank(m_dc)) {
            m_dc = m_dc.trim();
            logger.info("Data Center is set to [{}] by property 'idc' in server.properties.", m_dc);
            return;
        }

        // 4. Set Data Center to null.
        m_dc = null;
        logger.debug(
                "Data Center is set to null. Because it is not available in either (1) JVM system property 'idc', (2) OS env variable 'IDC' nor (3) property 'idc' from the properties InputStream.");
    }

    @Override
    public String toString() {
        return "environment [" + getEnvType() + "] data center [" + getDataCenter() + "] properties: "
                + m_serverProperties + " (DefaultServerProvider)";
    }
}