package com.example.my.apollo.portal.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.my.apollo.common.entity.App;
import com.example.my.apollo.portal.PortalApplication;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = PortalApplication.class)
@RunWith(SpringRunner.class)
public class AppRepositoryTest {

    @Autowired
    private AppRepository appRepository;

    @Test
    public void findByAppIdTest() {
        String appId = "demoApp";
        App app = appRepository.findByAppId(appId);
        Assert.assertNotNull(app);
    }

    @Test
    public void saveTest() {
        App entity = new App();
        entity.setAppId("demoApp");
        entity.setId(2);
        entity.setName("Demo App");
        entity.setOrgId("TEST1");
        entity.setOrgName("Sample Dept 1");
        entity.setOwnerName("apollo");
        entity.setOwnerEmail("apollo@acme.com");
        entity.setDataChangeCreatedBy("default");
        
        App app = appRepository.save(entity);
        Assert.assertNotNull(app);
    }

    @Test
    public void findByAppIdInTest() {
        Set<String> appIds = new HashSet<String>();
        appIds.add("SampleApp");
        appIds.add("demoApp");
        List<App> actuals = appRepository.findByAppIdIn(appIds);
        Assert.assertFalse(actuals.isEmpty());
    }

    @Test
    public void findByOrgNameTest() {
        String orgName = "Sample Dept 1";
        App actual = appRepository.findByOrgName(orgName);
        Assert.assertNotNull(actual);
    }

    @Test
    public void deleteAppTest() {
        String appId = "demoApp";
        String operator = "default";
        int id = appRepository.deleteApp(appId, operator);
        Assert.assertEquals(2, id);
    }

    @Test
    public void findByOwnerNameTest() {
        String ownerName = "apollo";
        List<App> actuals = appRepository.findByOwnerName(ownerName, PageRequest.of(0, 10));
        Assert.assertFalse(actuals.isEmpty());
    }
}