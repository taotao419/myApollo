package com.example.my.apollo.biz.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import com.example.my.apollo.biz.entity.Audit;
import com.example.my.apollo.biz.entity.Cluster;
import com.example.my.apollo.biz.repository.ClusterRepository;
import com.example.my.apollo.common.exception.ServiceException;
import com.example.my.apollo.core.ConfigConsts;
import com.google.common.base.Strings;

import org.springframework.stereotype.Service;

@Service
public class ClusterService {
    private final ClusterRepository clusterRepository;
    private final AuditService auditService;

    public ClusterService(final ClusterRepository clusterRepository, final AuditService auditService) {
        this.clusterRepository = clusterRepository;
        this.auditService = auditService;
    }

    public boolean isClusterNameUnique(String appId, String clusterName) {
        Objects.requireNonNull(appId, "AppId must not be null");
        Objects.requireNonNull(clusterName, "ClusterName must not be null");
        return Objects.isNull(clusterRepository.findByAppIdAndName(appId, clusterName));
    }

    public List<Cluster> findParentClusters(String appId) {
        if (Strings.isNullOrEmpty(appId)) {
            return Collections.emptyList();
        }

        List<Cluster> clusters = clusterRepository.findByAppIdAndParentClusterId(appId, 0L);
        if (clusters == null) {
            return Collections.emptyList();
        }

        Collections.sort(clusters);

        return clusters;
    }

    @Transactional
    public void createDefaultCluster(String appId, String createBy) {
        if (!isClusterNameUnique(appId, ConfigConsts.CLUSTER_NAME_DEFAULT)) {
            throw new ServiceException("cluster not unique");
        }
        Cluster cluster = new Cluster();
        cluster.setName(ConfigConsts.CLUSTER_NAME_DEFAULT);
        cluster.setAppId(appId);
        cluster.setDataChangeCreatedBy(createBy);
        cluster.setDataChangeLastModifiedBy(createBy);
        clusterRepository.save(cluster);

        auditService.audit(Cluster.class.getSimpleName(), cluster.getId(), Audit.OP.INSERT, createBy);
    }
}