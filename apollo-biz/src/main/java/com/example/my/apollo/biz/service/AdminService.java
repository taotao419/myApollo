package com.example.my.apollo.biz.service;

import javax.transaction.Transactional;

import com.example.my.apollo.common.entity.App;
import com.example.my.apollo.core.ConfigConsts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final static Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final AppService appService;
    private final AppNamespaceService appNamespaceService;
    private final ClusterService clusterService;
    private final NamespaceService namespaceService;
    
    public AdminService(
        final AppService appService,
        final @Lazy AppNamespaceService appNamespaceService,
        final @Lazy ClusterService clusterService,
        final @Lazy NamespaceService namespaceService) {
      this.appService = appService;
      this.appNamespaceService = appNamespaceService;
      this.clusterService = clusterService;
      this.namespaceService = namespaceService;
    }

    @Transactional
    public App createNewApp(App app) {
      String createBy = app.getDataChangeCreatedBy();
      App createdApp = appService.save(app);
  
      String appId = createdApp.getAppId();
  
      appNamespaceService.createDefaultAppNamespace(appId, createBy);
  
      clusterService.createDefaultCluster(appId, createBy);
  
      namespaceService.instanceOfAppNamespaces(appId, ConfigConsts.CLUSTER_NAME_DEFAULT, createBy);
  
      return app;
    }
}