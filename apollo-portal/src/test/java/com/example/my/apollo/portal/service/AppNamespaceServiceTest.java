package com.example.my.apollo.portal.service;

import com.example.my.apollo.common.entity.AppNamespace;
import com.example.my.apollo.portal.PortalApplication;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * AppNamespaceServiceTest
 */
@SpringBootTest(classes = PortalApplication.class)
@RunWith(SpringRunner.class)
public class AppNamespaceServiceTest {

    @Autowired
    private AppNamespaceService appNamespaceService;
  
    private final String APP = "app-test2";

    @Test
    public void testCreateDefaultAppNamespace() {
        appNamespaceService.createDefaultAppNamespace(APP);
    
        // AppNamespace appNamespace = appNamespaceService.findByAppIdAndName(APP, ConfigConsts.NAMESPACE_APPLICATION);
    
        // Assert.assertNotNull(appNamespace);
        // Assert.assertEquals(ConfigFileFormat.Properties.getValue(), appNamespace.getFormat());
    
      }
}