package com.example.my.apollo.biz.service;

import java.util.List;

import javax.transaction.Transactional;

import com.example.my.apollo.biz.entity.Audit;
import com.example.my.apollo.biz.entity.Namespace;
import com.example.my.apollo.biz.repository.NamespaceRepository;
import com.example.my.apollo.common.entity.AppNamespace;

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
}