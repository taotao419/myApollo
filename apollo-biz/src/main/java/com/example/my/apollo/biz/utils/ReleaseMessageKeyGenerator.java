package com.example.my.apollo.biz.utils;

import com.example.my.apollo.core.ConfigConsts;
import com.google.common.base.Joiner;

/**
 * ReleaseMessageKeyGenerator
 */
public class ReleaseMessageKeyGenerator {
    private static final Joiner STRING_JOINER = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR);

    public static String generate(String appId, String cluster, String namespace) {
      return STRING_JOINER.join(appId, cluster, namespace);
    }
    
}