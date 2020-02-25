package com.example.my.apollo.portal.service;

import com.example.my.apollo.common.dto.AppDTO;
import com.example.my.apollo.common.entity.App;
import com.example.my.apollo.core.enums.Env;
import com.example.my.apollo.portal.AbstractUnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AppServiceTest extends AbstractUnitTest {

    @Autowired
    private AppService appService;

    @Test
    public void testLoadByEnvAndAppId() {
        String appId = "unitTestApp-3";
        AppDTO app = appService.load(Env.DEV, appId);
        Assert.assertNotNull(app);
    }

    @Test
    public void testCreateAppInRemote(){
        App app = new App();
        app.setAppId("unitTestApp-0225");
        app.setId(2);
        app.setName("UNIT TEST App");
        app.setOrgId("TEST1");
        app.setOrgName("Sample Dept 1");
        app.setOwnerName("apollo");
        app.setOwnerEmail("apollo@acme.com");
        app.setDataChangeCreatedBy("zkl");
        
        appService.createAppInRemote(Env.DEV, app);
    }

}