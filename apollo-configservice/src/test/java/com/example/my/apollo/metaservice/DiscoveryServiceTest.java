package com.example.my.apollo.metaservice;

import java.util.List;

import com.example.my.apollo.AbstractIntegrationTest;
import com.example.my.apollo.metaservice.service.DiscoveryService;
import com.netflix.appinfo.InstanceInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.junit.Assert;
import org.junit.Test;

/**
 * DiscoveryServiceTest
 */
public class DiscoveryServiceTest extends AbstractIntegrationTest {
    @Autowired
    private DiscoveryService discoveryService;

    @Test
    public void testGetConfigServiceInstances() {
        List<InstanceInfo> actuals = discoveryService.getConfigServiceInstances();
        Assert.assertFalse(actuals.isEmpty());
    }

    @Test
    public void testGetAdminServiceInstances(){
        List<InstanceInfo> actuals = discoveryService.getAdminServiceInstances();
        Assert.assertFalse(actuals.isEmpty());
    }

    @Test
    public void testGetMetaServiceInstances(){
        List<InstanceInfo> actuals =   discoveryService.getMetaServiceInstances();
        Assert.assertFalse(actuals.isEmpty());
    }
}