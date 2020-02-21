package com.example.my.apollo.biz.repository;

import com.example.my.apollo.biz.AbstractIntegrationTest;
import com.example.my.apollo.common.entity.App;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * AppRepositoryTest
 */
public class AppRepositoryTest extends AbstractIntegrationTest {
  @Autowired
  private AppRepository appRepository;

  @Test
  public void testFindByAppId() {
    String appId = "demoApp";
    App actual = appRepository.findByAppId(appId);
    Assert.assertNotNull(actual);
  }

  @Test
  public void test() {
    long actual = appRepository.count();
    Assert.assertEquals(1, actual);
  }

  @Test
  public void testCreate() {
    String appId = "someAppId";
    String appName = "someAppName";
    String ownerName = "someOwnerName";
    String ownerEmail = "someOwnerName@ctrip.com";
    String dataChangeCreatedBy = "default";

    App app = new App();
    app.setAppId(appId);
    app.setName(appName);
    app.setOwnerName(ownerName);
    app.setOwnerEmail(ownerEmail);
    app.setDataChangeCreatedBy(dataChangeCreatedBy);

    // Assert.assertEquals(0, appRepository.count());

    appRepository.save(app);

    Assert.assertEquals(1, appRepository.count());
  }

  @Test
  public void testRemove() {
    String appId = "someAppId";
    String appName = "someAppName";
    String ownerName = "someOwnerName";
    String ownerEmail = "someOwnerName@ctrip.com";

    App app = new App();
    app.setAppId(appId);
    app.setName(appName);
    app.setOwnerName(ownerName);
    app.setOwnerEmail(ownerEmail);

    Assert.assertEquals(0, appRepository.count());

    appRepository.save(app);

    Assert.assertEquals(1, appRepository.count());

    appRepository.deleteById(app.getId());

    Assert.assertEquals(0, appRepository.count());
  }
}