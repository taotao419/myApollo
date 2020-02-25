package com.example.my.apollo.biz.service;

import com.example.my.apollo.biz.AbstractIntegrationTest;
import com.example.my.apollo.common.entity.App;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

/**
 * AdminServiceTest
 */
public class AdminServiceTest extends AbstractIntegrationTest {

    @Autowired
    private AdminService adminService;

    @Test
    @Rollback(false)
    public void testCreateNewApp(){
        App app=new App();
        app.setAppId("unitTestApp-2");
        app.setName("appName");
        app.setOrgId("TEST1");
        app.setOrgName("UnitTest Dept 1");
        app.setOwnerEmail("zkl@msn.com");
        app.setOwnerName("zkl");
        app.setDataChangeCreatedBy("zkl");

        adminService.createNewApp(app);
    }

    
}