package com.example.my.apollo.portal.service;

import java.util.Objects;

import com.example.my.apollo.common.entity.AppNamespace;
import com.example.my.apollo.common.exception.BadRequestException;
import com.example.my.apollo.core.enums.ConfigFileFormat;
import com.example.my.apollo.portal.repository.AppNamespaceRepository;
import com.example.my.apollo.portal.spi.UserInfoHolder;

import org.springframework.stereotype.Service;

/**
 * AppNamespaceService
 */
@Service
public class AppNamespaceService {

    private final UserInfoHolder userInfoHolder;
    private final AppNamespaceRepository appNamespaceRepository;

    public AppNamespaceService(
        final UserInfoHolder userInfoHolder,
        final AppNamespaceRepository appNamespaceRepository){
            this.userInfoHolder=userInfoHolder;
            this.appNamespaceRepository=appNamespaceRepository;
    }
    
    public void createDefaultAppNamespace(String appId) {
        //1.先校验 app是否已经存在了默认的Namespace["application"]
        if (!isAppNamespaceNameUnique(appId, ConfigConsts.NAMESPACE_APPLICATION)) {
          throw new BadRequestException(String.format("App already has application namespace. AppId = %s", appId));
        }
    
        //2. 赋值给object
        AppNamespace appNs = new AppNamespace();
        appNs.setAppId(appId);
        appNs.setName(ConfigConsts.NAMESPACE_APPLICATION);
        appNs.setComment("default app namespace");
        appNs.setFormat(ConfigFileFormat.Properties.getValue());
        String userId = userInfoHolder.getUser().getUserId();
        appNs.setDataChangeCreatedBy(userId);
        appNs.setDataChangeLastModifiedBy(userId);
    
        //3. 保存到DB
        appNamespaceRepository.save(appNs);
      }
      
      public boolean isAppNamespaceNameUnique(String appId, String namespaceName) {
        Objects.requireNonNull(appId, "AppId must not be null");
        Objects.requireNonNull(namespaceName, "Namespace must not be null");
        return Objects.isNull(appNamespaceRepository.findByAppIdAndName(appId, namespaceName));
      }

      private static class ConfigConsts{
        public static String NAMESPACE_APPLICATION = "application";
      }
}