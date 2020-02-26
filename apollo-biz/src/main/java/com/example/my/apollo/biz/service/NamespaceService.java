package com.example.my.apollo.biz.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import com.example.my.apollo.biz.entity.Audit;
import com.example.my.apollo.biz.entity.Namespace;
import com.example.my.apollo.biz.repository.NamespaceRepository;
import com.example.my.apollo.common.entity.AppNamespace;
import com.example.my.apollo.common.exception.ServiceException;

import org.springframework.stereotype.Service;

@Service
public class NamespaceService {

    private final AppNamespaceService appNamespaceService;
    private final NamespaceRepository namespaceRepository;
    private final AuditService auditService;

    public NamespaceService(
        final AppNamespaceService appNamespaceService,
        final NamespaceRepository namespaceRepository,
        final AuditService auditService
    ) {
        this.appNamespaceService=appNamespaceService;
        this.namespaceRepository=namespaceRepository;
        this.auditService=auditService;
    }

    @Transactional
    public void instanceOfAppNamespaces(String appId, String clusterName, String createBy) {
      //获取所有的AppNamespace对象
      List<AppNamespace> appNamespaces = appNamespaceService.findByAppId(appId);
  
      for (AppNamespace appNamespace : appNamespaces) {
        Namespace ns = new Namespace();
        ns.setAppId(appId);
        ns.setClusterName(clusterName);
        ns.setNamespaceName(appNamespace.getName());
        ns.setDataChangeCreatedBy(createBy);
        ns.setDataChangeLastModifiedBy(createBy);
        namespaceRepository.save(ns);
        auditService.audit(Namespace.class.getSimpleName(), ns.getId(), Audit.OP.INSERT, createBy);
      }
  
    } 

    public boolean isNamespaceUnique(String appId, String cluster, String namespace) {
      Objects.requireNonNull(appId, "AppId must not be null");
      Objects.requireNonNull(cluster, "Cluster must not be null");
      Objects.requireNonNull(namespace, "Namespace must not be null");
      return Objects.isNull(
          namespaceRepository.findByAppIdAndClusterNameAndNamespaceName(appId, cluster, namespace));
    }

    public List<Namespace> findByAppIdAndNamespaceName(String appId, String namespaceName) {
      return namespaceRepository.findByAppIdAndNamespaceNameOrderByIdAsc(appId, namespaceName);
    }

    public Namespace findOne(String appId, String clusterName, String namespaceName) {
      return namespaceRepository.findByAppIdAndClusterNameAndNamespaceName(appId, clusterName,
                                                                           namespaceName);
    }

    @Transactional
    public Namespace save(Namespace entity) {
      if (!isNamespaceUnique(entity.getAppId(), entity.getClusterName(), entity.getNamespaceName())) {
        throw new ServiceException("namespace not unique");
      }
      entity.setId(0);//protection
      Namespace namespace = namespaceRepository.save(entity);
  
      //记录Audit到数据库中
      auditService.audit(Namespace.class.getSimpleName(), namespace.getId(), Audit.OP.INSERT,
                         namespace.getDataChangeCreatedBy());
  
      return namespace;
    }
}