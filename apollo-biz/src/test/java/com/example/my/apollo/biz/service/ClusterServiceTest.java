package com.example.my.apollo.biz.service;

import java.util.List;

import com.example.my.apollo.biz.AbstractIntegrationTest;
import com.example.my.apollo.biz.entity.Cluster;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

/**
 * ClusterServiceTest
 */
public class ClusterServiceTest extends AbstractIntegrationTest {
    @Autowired
    private ClusterService clusterService;

    @Test
    @Rollback(false)
    public void testCreateDefaultCluster() {
        String appId = "unitTestApp";
        String createBy = "zkl";
        clusterService.createDefaultCluster(appId, createBy);
    }

    @Test
    public void testFindParentClusters() {
        String appId = "unitTestApp-0225";
        List<Cluster> actuals = clusterService.findParentClusters(appId);
        Assert.assertFalse(actuals.isEmpty());
    }

}