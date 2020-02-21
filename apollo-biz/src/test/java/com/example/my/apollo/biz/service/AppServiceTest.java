package com.example.my.apollo.biz.service;

import com.example.my.apollo.biz.AbstractIntegrationTest;
import com.example.my.apollo.common.entity.App;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

/**
 * AppServiceTest
 */
public class AppServiceTest extends AbstractIntegrationTest {

    @Autowired
    private AppService appService;// 不能写final,因为final就是readonly 不能更改了,这个还等着spring赋值呢.

    @Test
    public void testFindOne() {
        String appId = "SampleApp";
        App actual = appService.findOne(appId);
        Assert.assertNotNull(actual);
    }

    @Test
    @Rollback(false)
    public void test() {
        App entity = new App();
        entity.setAppId("demoApp");
        entity.setId(2);
        entity.setName("Demo App");
        entity.setOrgId("TEST1");
        entity.setOrgName("Sample Dept 1");
        entity.setOwnerName("apollo");
        entity.setOwnerEmail("apollo@acme.com");
        entity.setDataChangeCreatedBy("default");

        App actual = appService.save(entity);
        Assert.assertNotNull(actual);
    }

}