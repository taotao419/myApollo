package com.example.my.apollo.biz.service;

import com.example.my.apollo.biz.AbstractIntegrationTest;

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

}