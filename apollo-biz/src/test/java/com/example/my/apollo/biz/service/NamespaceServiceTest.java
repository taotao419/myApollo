package com.example.my.apollo.biz.service;

import com.example.my.apollo.biz.AbstractIntegrationTest;
import com.example.my.apollo.biz.entity.Namespace;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

/**
 * NamespaceServiceTest
 */
public class NamespaceServiceTest extends AbstractIntegrationTest {

    @Autowired
    private NamespaceService namespaceService;

    @Test
    @Rollback(false)
    public void testSave() {
        Namespace entity = new Namespace();
        entity.setAppId("unitTestApp-0225-2");
        entity.setNamespaceName("application-ut");
        entity.setClusterName("default");
        entity.setDataChangeCreatedBy("zkl");
        namespaceService.save(entity);
    }

}