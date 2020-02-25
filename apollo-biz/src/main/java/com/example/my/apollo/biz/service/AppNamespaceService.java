package com.example.my.apollo.biz.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import com.example.my.apollo.biz.entity.Audit;
import com.example.my.apollo.biz.repository.AppNamespaceRepository;
import com.example.my.apollo.common.entity.AppNamespace;
import com.example.my.apollo.common.exception.ServiceException;
import com.example.my.apollo.core.ConfigConsts;
import com.example.my.apollo.core.enums.ConfigFileFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AppNamespaceService {

    private static final Logger logger = LoggerFactory.getLogger(AppNamespaceService.class);

    private final AppNamespaceRepository appNamespaceRepository;
    private final AuditService auditService;

    public AppNamespaceService(final AppNamespaceRepository appNamespaceRepository, final AuditService auditService) {
        this.appNamespaceRepository = appNamespaceRepository;
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
}