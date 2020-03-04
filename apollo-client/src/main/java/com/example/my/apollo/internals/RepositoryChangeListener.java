package com.example.my.apollo.internals;

import java.util.Properties;

/**
 * RepositoryChangeListener
 */
public interface RepositoryChangeListener {

    /**
     * Invoked when config repository changes.
     * 
     * @param namespace     the namespace of this repository change
     * @param newProperties the properties after change
     */
    public void onRepositoryChange(String namespace, Properties newProperties);
}