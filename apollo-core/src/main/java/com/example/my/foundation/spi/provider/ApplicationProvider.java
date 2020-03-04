package com.example.my.foundation.spi.provider;

import java.io.InputStream;

/**
 * ApplicationProvider
 */
public interface ApplicationProvider extends Provider {

    /**
     * @return the application's app id
     */
    public String getAppId();

    /**
     * @return whether the application's app id is set or not
     */
    public boolean isAppIdSet();

    /**
     * Initialize the application provider with the specified input stream
     */
    public void initialize(InputStream in);
}