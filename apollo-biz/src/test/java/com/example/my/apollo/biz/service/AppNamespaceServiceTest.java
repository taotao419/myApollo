package com.example.my.apollo.biz.service;

import com.example.my.apollo.biz.AbstractIntegrationTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

/**
 * AppNamespaceServiceTest
 */
public class AppNamespaceServiceTest extends AbstractIntegrationTest {
    @Autowired
    private AppNamespaceService appNamespaceService;

    @Test
    @Rollback(false)
    public void testCreateDefaultAppNamespace() {
        String appId = "unitTestApp";
        String createBy = "zkl";
        appNamespaceService.createDefaultAppNamespace(appId, createBy);
    }

}