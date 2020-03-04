package com.example.my.apollo.util;

import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.RateLimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ConfigUtil
 */
public class ConfigUtil {
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);
    private int refreshInterval = 5;
    private TimeUnit refreshIntervalTimeUnit = TimeUnit.MINUTES;
    private int connectTimeout = 1000; // 1 second
    private int readTimeout = 5000; // 5 seconds
    private String cluster;
    private int loadConfigQPS = 2; // 2 times per second
    private int longPollQPS = 2; // 2 times per second
    // for on error retry
    private long onErrorRetryInterval = 1;// 1 second
    private TimeUnit onErrorRetryIntervalTimeUnit = TimeUnit.SECONDS;// 1 second
    // for typed config cache of parser result, e.g. integer, double, long, etc.
    private long maxConfigCacheSize = 500;// 500 cache key
    private long configCacheExpireTime = 1;// 1 minute
    private TimeUnit configCacheExpireTimeUnit = TimeUnit.MINUTES;// 1 minute
    private long longPollingInitialDelayInMills = 2000;// 2 seconds
    private boolean autoUpdateInjectedSpringProperties = true;
    private final RateLimiter warnLogRateLimiter;

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }
}