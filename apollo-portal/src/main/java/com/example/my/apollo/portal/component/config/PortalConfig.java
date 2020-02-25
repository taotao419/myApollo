package com.example.my.apollo.portal.component.config;

import org.springframework.stereotype.Component;

@Component
public class PortalConfig {

    public int connectTimeout() {
        return 3000;
    }

    public int readTimeout() {
        return 10000;
    }
}