package com.example.my.apollo.biz.config;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.springframework.stereotype.Component;

@Component
public class BizConfig {

    private static final int DEFAULT_ITEM_KEY_LENGTH = 128;
    private static final int DEFAULT_ITEM_VALUE_LENGTH = 20000;
    private static final int DEFAULT_APPNAMESPACE_CACHE_REBUILD_INTERVAL = 60; // 60s
    private static final int DEFAULT_GRAY_RELEASE_RULE_SCAN_INTERVAL = 60; // 60s
    private static final int DEFAULT_APPNAMESPACE_CACHE_SCAN_INTERVAL = 1; // 1s
    private static final int DEFAULT_RELEASE_MESSAGE_CACHE_SCAN_INTERVAL = 1; // 1s
    private static final int DEFAULT_RELEASE_MESSAGE_SCAN_INTERVAL_IN_MS = 1000; // 1000ms
    private static final int DEFAULT_RELEASE_MESSAGE_NOTIFICATION_BATCH = 100;
    private static final int DEFAULT_RELEASE_MESSAGE_NOTIFICATION_BATCH_INTERVAL_IN_MILLI = 100;// 100ms
    private static final int DEFAULT_LONG_POLLING_TIMEOUT = 60; // 60s
    private static final String LIST_SEPARATOR = ",";
    private Splitter splitter = Splitter.on(LIST_SEPARATOR).omitEmptyStrings().trimResults();

    private Gson gson = new Gson();
    private static final Type namespaceValueLengthOverrideTypeReference = new TypeToken<Map<Long, Integer>>() {
    }.getType();

    public List<String> eurekaServiceUrls() {
        String configuration = "http://localhost:8060/eureka/"; // getValue("eureka.service.url", "");
        if (Strings.isNullOrEmpty(configuration)) {
            return Collections.emptyList();
        }

        return splitter.splitToList(configuration);
    }

    public Map<Long, Integer> namespaceValueLengthLimitOverride() {
        String namespaceValueLengthOverrideString = "";// getValue("namespace.value.length.limit.override");
        Map<Long, Integer> namespaceValueLengthOverride = Maps.newHashMap();// 即 new HashMap<Long,Integer>();
        if (!Strings.isNullOrEmpty(namespaceValueLengthOverrideString)) {
            namespaceValueLengthOverride = gson.fromJson(namespaceValueLengthOverrideString,
                    namespaceValueLengthOverrideTypeReference);
        }

        return namespaceValueLengthOverride;
    }

    public int itemKeyLengthLimit() {
        int limit = DEFAULT_ITEM_KEY_LENGTH;
        return checkInt(limit, 5, Integer.MAX_VALUE, DEFAULT_ITEM_KEY_LENGTH);
    }

    public int itemValueLengthLimit() {
        int limit = DEFAULT_ITEM_VALUE_LENGTH;
        return checkInt(limit, 5, Integer.MAX_VALUE, DEFAULT_ITEM_VALUE_LENGTH);
    }

    /**
     * 60 seconds
     */
    public long longPollingTimeoutInMilli() {
        int timeout = DEFAULT_LONG_POLLING_TIMEOUT;
        // java client's long polling timeout is 90 seconds, so server side long polling
        // timeout must be less than 90
        return 1000 * checkInt(timeout, 1, 90, DEFAULT_LONG_POLLING_TIMEOUT); // 60 seconds
    }

    public int appNamespaceCacheScanInterval() {
        int interval = DEFAULT_APPNAMESPACE_CACHE_SCAN_INTERVAL;
        return checkInt(interval, 1, Integer.MAX_VALUE, DEFAULT_APPNAMESPACE_CACHE_SCAN_INTERVAL);
    }

    public TimeUnit appNamespaceCacheScanIntervalTimeUnit() {
        return TimeUnit.SECONDS;
    }

    public int appNamespaceCacheRebuildInterval() {
        int interval = DEFAULT_APPNAMESPACE_CACHE_REBUILD_INTERVAL;
        return checkInt(interval, 1, Integer.MAX_VALUE, DEFAULT_APPNAMESPACE_CACHE_REBUILD_INTERVAL);
    }

    public TimeUnit appNamespaceCacheRebuildIntervalTimeUnit() {
        return TimeUnit.SECONDS;
    }

    public int releaseMessageScanIntervalInMilli() {
        int interval = DEFAULT_RELEASE_MESSAGE_SCAN_INTERVAL_IN_MS;
        return checkInt(interval, 100, Integer.MAX_VALUE, DEFAULT_RELEASE_MESSAGE_SCAN_INTERVAL_IN_MS);
    }

    public int releaseMessageCacheScanInterval() {
        int interval = DEFAULT_RELEASE_MESSAGE_CACHE_SCAN_INTERVAL;
        return checkInt(interval, 1, Integer.MAX_VALUE, DEFAULT_RELEASE_MESSAGE_CACHE_SCAN_INTERVAL);
    }

    public TimeUnit releaseMessageCacheScanIntervalTimeUnit() {
        return TimeUnit.SECONDS;
    }

    /**
     * batch size = 100
     */
    public int releaseMessageNotificationBatch() {
        int batch = DEFAULT_RELEASE_MESSAGE_NOTIFICATION_BATCH;
        return checkInt(batch, 1, Integer.MAX_VALUE, DEFAULT_RELEASE_MESSAGE_NOTIFICATION_BATCH);
    }

    public int releaseMessageNotificationBatchIntervalInMilli() {
        int interval = DEFAULT_RELEASE_MESSAGE_NOTIFICATION_BATCH_INTERVAL_IN_MILLI;
        return checkInt(interval, 10, Integer.MAX_VALUE, DEFAULT_RELEASE_MESSAGE_NOTIFICATION_BATCH_INTERVAL_IN_MILLI);
    }

    private int checkInt(int value, int min, int max, int defaultValue) {
        if (value >= min && value <= max) {
            return value;
        }
        return defaultValue;
    }
}