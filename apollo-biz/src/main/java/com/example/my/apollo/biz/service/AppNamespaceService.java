package com.example.my.apollo.biz.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import com.example.my.apollo.biz.entity.Audit;
import com.example.my.apollo.biz.entity.Cluster;
import com.example.my.apollo.biz.entity.Namespace;
import com.example.my.apollo.biz.repository.AppNamespaceRepository;
import com.example.my.apollo.common.entity.AppNamespace;
import com.example.my.apollo.common.exception.ServiceException;
import com.example.my.apollo.core.ConfigConsts;
import com.example.my.apollo.core.enums.ConfigFileFormat;
import com.example.my.apollo.core.utils.StringUtils;
import com.google.common.base.Preconditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class AppNamespaceService {

    private static final Logger logger = LoggerFactory.getLogger(AppNamespaceService.class);

    private final AppNamespaceRepository appNamespaceRepository;
    private final NamespaceService namespaceService;
    private final AuditService auditService;
    private final ClusterService clusterService;

    public AppNamespaceService(final AppNamespaceRepository appNamespaceRepository,
    final @Lazy NamespaceService namespaceService,
    final @Lazy ClusterService clusterService,
    final AuditService auditService) {
        this.appNamespaceRepository = appNamespaceRepository;
        this.namespaceService=namespaceService;
        this.clusterService = clusterService;
        this.auditService = auditService;
    }

    public boolean isAppNamespaceNameUnique(String appId, String namespaceName) {
        Objects.requireNonNull(appId, "AppId must not be null");
        Objects.requireNonNull(namespaceName, "Namespace must not be null");
        return Objects.isNull(appNamespaceRepository.findByAppIdAndName(appId, namespaceName));
    }

    public List<AppNamespace> findByAppId(String appId) {
        return appNamespaceRepository.findByAppId(appId);
    }

    public AppNamespace findOne(String appId, String namespaceName) {
        Preconditions.checkArgument(!StringUtils.isContainEmpty(appId, namespaceName),
                "appId or Namespace must not be null");
        return appNamespaceRepository.findByAppIdAndName(appId, namespaceName);
    }

    @Transactional
    public void createDefaultAppNamespace(String appId, String createBy) {
        if (!isAppNamespaceNameUnique(appId, ConfigConsts.NAMESPACE_APPLICATION)) {
            throw new ServiceException("appnamespace not unique");
        }
        AppNamespace appNs = new AppNamespace();
        appNs.setAppId(appId);
        appNs.setName(ConfigConsts.NAMESPACE_APPLICATION);
        appNs.setComment("default app namespace");
        appNs.setFormat(ConfigFileFormat.Properties.getValue());
        appNs.setDataChangeCreatedBy(createBy);
        appNs.setDataChangeLastModifiedBy(createBy);
        appNamespaceRepository.save(appNs);

        auditService.audit(AppNamespace.class.getSimpleName(), appNs.getId(), Audit.OP.INSERT, createBy);
    }

    @Transactional
    public AppNamespace createAppNamespace(AppNamespace appNamespace) {
        String createBy = appNamespace.getDataChangeCreatedBy();
        // 1.防御代码
        if (!isAppNamespaceNameUnique(appNamespace.getAppId(), appNamespace.getName())) {
            throw new ServiceException("appnamespace not unique");
        }
        // 2.公共字段赋值
        appNamespace.setId(0);// protection
        appNamespace.setDataChangeCreatedBy(createBy);
        appNamespace.setDataChangeLastModifiedBy(createBy);
        // 3.保存到数据库
        appNamespace = appNamespaceRepository.save(appNamespace);

        createNamespaceForAppNamespaceInAllCluster(appNamespace.getAppId(), appNamespace.getName(), createBy);

        auditService.audit(AppNamespace.class.getSimpleName(), appNamespace.getId(), Audit.OP.INSERT, createBy);
        return appNamespace;
    }

    public void createNamespaceForAppNamespaceInAllCluster(String appId, String namespaceName, String createBy) {
        List<Cluster> clusters = clusterService.findParentClusters(appId);

        for (Cluster cluster : clusters) {

            // in case there is some dirty data, e.g. public namespace deleted in other app
            // and now created in this app
            if (!namespaceService.isNamespaceUnique(appId, cluster.getName(), namespaceName)) {
                continue;
            }

            Namespace namespace = new Namespace();
            namespace.setClusterName(cluster.getName());
            namespace.setAppId(appId);
            namespace.setNamespaceName(namespaceName);
            namespace.setDataChangeCreatedBy(createBy);
            namespace.setDataChangeLastModifiedBy(createBy);

            namespaceService.save(namespace);
        }
    }

    @Transactional
    public void batchDelete(String appId, String operator) {
      appNamespaceRepository.batchDeleteByAppId(appId, operator);
    }
  
    // @Transactional
    // public void deleteAppNamespace(AppNamespace appNamespace, String operator) {
    //   String appId = appNamespace.getAppId();
    //   String namespaceName = appNamespace.getName();
  
    //   logger.info("{} is deleting AppNamespace, appId: {}, namespace: {}", operator, appId, namespaceName);
  
    //   // 1. delete namespaces
    //   List<Namespace> namespaces = namespaceService.findByAppIdAndNamespaceName(appId, namespaceName);
  
    //   if (namespaces != null) {
    //     for (Namespace namespace : namespaces) {
    //       namespaceService.deleteNamespace(namespace, operator);
    //     }
    //   }
  
    //   // 2. delete app namespace
    //   appNamespaceRepository.delete(appId, namespaceName, operator);
    // }
}