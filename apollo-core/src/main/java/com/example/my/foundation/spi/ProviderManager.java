package com.example.my.foundation.spi;

import com.example.my.foundation.spi.provider.Provider;

public interface ProviderManager {

    public String getProperty(String name,String defaultValue);

    public <T extends Provider> T provider(Class<T> clazz);
}