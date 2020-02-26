package com.example.my.apollo.biz.config;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    private static final String LIST_SEPARATOR = ",";
    private Splitter splitter = Splitter.on(LIST_SEPARATOR).omitEmptyStrings().trimResults();

    private Gson gson = new Gson();
    private static final Type namespaceValueLengthOverrideTypeReference = new TypeToken<Map<Long, Integer>>() {
    }.getType();

    public List<String> eurekaServiceUrls() {
        String configuration = "http://localhost:8060/eureka/";  //getValue("eureka.service.url", "");
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

    private int checkInt(int value, int min, int max, int defaultValue) {
        if (value >= min && value <= max) {
            return value;
        }
        return defaultValue;
    }
}