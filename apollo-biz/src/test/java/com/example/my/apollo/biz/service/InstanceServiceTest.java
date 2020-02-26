package com.example.my.apollo.biz.service;

import com.example.my.apollo.biz.AbstractIntegrationTest;
import com.example.my.apollo.biz.entity.Instance;
import com.example.my.apollo.biz.entity.InstanceConfig;
import com.google.common.base.Preconditions;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.junit.Assert;

public class InstanceServiceTest extends AbstractIntegrationTest {

    @Autowired
    private InstanceService instanceService;

    @Test
    public void testPreconditions() {
        // 就是 if (!expression) {
        // throw new IllegalArgumentException(String.valueOf(errorMessage));
        // }
        Object checkObject = null;
        Preconditions.checkArgument(checkObject != null, String.format("Instance config %d doesn't exist", 100));
    }

    @Test
    @Rollback(false)
    public void testCreateInstance() {
        Instance instance = new Instance();
        instance.setAppId("unitTestApp-0226-1");
        instance.setClusterName("default");
        instance.setDataCenter("SH-DataCenter");
        instance.setIp("10.0.0.1");

        Instance actual = instanceService.createInstance(instance);
        Assert.assertNotNull(actual);
    }

    @Test
    public void testFindActiveInstanceConfigsByReleaseKey() {
        String releaseKey = "releaseKey";
        Page<InstanceConfig> actuals = instanceService.findActiveInstanceConfigsByReleaseKey(releaseKey,
                PageRequest.of(0, 10));
        Assert.assertTrue(actuals.getSize() > 0);
    }
}
