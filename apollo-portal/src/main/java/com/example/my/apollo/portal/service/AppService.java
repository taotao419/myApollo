package com.example.my.apollo.portal.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.example.my.apollo.common.dto.AppDTO;
import com.example.my.apollo.common.dto.PageDTO;
import com.example.my.apollo.common.entity.App;
import com.example.my.apollo.core.enums.Env;
import com.example.my.apollo.portal.repository.AppRepository;
import com.example.my.apollo.portal.spi.UserInfoHolder;
import com.google.common.collect.Lists;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AppService {

    private final UserInfoHolder userInfoHolder;
    private final AppRepository appRepository;
    private final AppNamespaceService appNamespaceService;

    public AppService(
            UserInfoHolder userInfoHolder, 
            AppRepository appRepository,
            AppNamespaceService appNamespaceService) {
        this.userInfoHolder = userInfoHolder;
        this.appRepository = appRepository;
        this.appNamespaceService = appNamespaceService;
    }

    public List<App> findAll() {
        Iterable<App> apps = appRepository.findAll();
        if (apps == null) {
          return Collections.emptyList();
        }
        return Lists.newArrayList(apps);
      }
    
      public PageDTO<App> findAll(Pageable pageable) {
        Page<App> apps = appRepository.findAll(pageable);
    
        return new PageDTO<>(apps.getContent(), pageable, apps.getTotalElements());
      }
    
      public PageDTO<App> searchByAppIdOrAppName(String query, Pageable pageable) {
        Page<App> apps = appRepository.findByAppIdContainingOrNameContaining(query, query, pageable);
    
        return new PageDTO<>(apps.getContent(), pageable, apps.getTotalElements());
      }
    
      public List<App> findByAppIds(Set<String> appIds) {
        return appRepository.findByAppIdIn(appIds);
      }
    
      public List<App> findByAppIds(Set<String> appIds, Pageable pageable) {
        return appRepository.findByAppIdIn(appIds, pageable);
      }
    
      public List<App> findByOwnerName(String ownerName, Pageable page) {
        return appRepository.findByOwnerName(ownerName, page);
      }
    
      public App load(String appId) {
        return appRepository.findByAppId(appId);
      }
    
     // public AppDTO load(Env env, String appId) {
     //   return appAPI.loadApp(env, appId);
     // }
}