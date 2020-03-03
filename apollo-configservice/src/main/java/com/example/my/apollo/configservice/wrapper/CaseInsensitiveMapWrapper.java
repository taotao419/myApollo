package com.example.my.apollo.configservice.wrapper;

import java.util.Map;

/**
 * CaseInsensitiveMapWrapper,把key全都改成小写.这样就不会大小写敏感了.
 */
public class CaseInsensitiveMapWrapper<T> {
    private final Map<String, T> delegate;

    public CaseInsensitiveMapWrapper(Map<String, T> delegate) {
        this.delegate = delegate;
    }

    public T get(String key) {
        return delegate.get(key.toLowerCase());
    }

    public T put(String key, T value) {
        return delegate.put(key.toLowerCase(), value);
    }

    public T remove(String key) {
        return delegate.remove(key.toLowerCase());
    }

    public int getSize() {
        return delegate.size();
    }
}