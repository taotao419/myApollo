package com.example.my.apollo.configservice.util;

import com.example.my.apollo.common.entity.AppNamespace;
import com.example.my.apollo.configservice.service.AppNamespaceServiceWithCache;

import org.springframework.stereotype.Component;

@Component
public class NamespaceUtil {
    private final AppNamespaceServiceWithCache appNamespaceServiceWithCache;

    public NamespaceUtil(final AppNamespaceServiceWithCache appNamespaceServiceWithCache) {
        this.appNamespaceServiceWithCache=appNamespaceServiceWithCache;
    }
    
    public String filterNamespaceName(String namespaceName) {
        if (namespaceName.toLowerCase().endsWith(".properties")) {
          int dotIndex = namespaceName.lastIndexOf(".");
          return namespaceName.substring(0, dotIndex);
        }
    
        return namespaceName;
      }
    
      /**
       * 根据AppId+namespaceName 查询AppNamespace entity.
       * 如果能从全局namespacName查到也可以.  
       */
      public String normalizeNamespace(String appId, String namespaceName) {
        AppNamespace appNamespace = appNamespaceServiceWithCache.findByAppIdAndNamespace(appId, namespaceName);
        if (appNamespace != null) {
          return appNamespace.getName();
        }
    
        appNamespace = appNamespaceServiceWithCache.findPublicNamespaceByName(namespaceName);
        if (appNamespace != null) {
          return appNamespace.getName();
        }
    
        return namespaceName;
      }
}