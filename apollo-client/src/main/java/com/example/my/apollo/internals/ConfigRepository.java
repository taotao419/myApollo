package com.example.my.apollo.internals;

import java.util.Properties;

import com.example.my.apollo.enums.ConfigSourceType;

/**
 * ConfigRepository
 */
public interface ConfigRepository {
    /**
     * Get the config from this repository.
     * 
     * @return config
     */
    public Properties getConfig();

    /**
     * Set the fallback repo for this repository.
     * 设置上游的 Repository 。
     * 主要用于 LocalFileConfigRepository ，从 Config Service 读取配置，缓存在本地文件。
     * @param upstreamConfigRepository the upstream repo
     */
    public void setUpstreamRepository(ConfigRepository upstreamConfigRepository);

    /**
     * Add change listener.
     * 
     * @param listener the listener to observe the changes
     */
    public void addChangeListener(RepositoryChangeListener listener);

    /**
     * Remove change listener.
     * 
     * @param listener the listener to remove
     */
    public void removeChangeListener(RepositoryChangeListener listener);

    /**
     * Return the config's source type, i.e. where is the config loaded from
     *
     * @return the config's source type
     */
    public ConfigSourceType getSourceType();

}