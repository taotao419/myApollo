package com.example.my.apollo.biz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import com.example.my.apollo.biz.AbstractIntegrationTest;
import com.example.my.apollo.biz.entity.Item;
import com.example.my.apollo.biz.repository.ItemRepository;
import com.example.my.apollo.common.exception.NotFoundException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * ItemServiceTest
 */
public class ItemServiceTest extends AbstractIntegrationTest {

    @Autowired
    private ItemService itemService;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testFindOne_shouldThrowNotFoundException() {
        String appId = "N/A";
        String clusterName = "";
        String namespaceName = "";
        String key = "";

        thrown.expect(NotFoundException.class);
        itemService.findOne(appId, clusterName, namespaceName, key);
    }

    @Test
    public void testFindOne() {
        String appId = "SampleApp";
        String clusterName = "default";
        String namespaceName = "application";
        String key = "timeout";

        Item actual = itemService.findOne(appId, clusterName, namespaceName, key);
        Assert.assertNotNull(actual);
        Assert.assertEquals("100", actual.getValue());
    }

    @Test
    @Rollback(false)
    public void testSave() {
        Item entity = new Item();
        entity.setNamespaceId(1);
        entity.setKey("switch-UT-2");
        entity.setValue("true");
        entity.setLineNum(1);
   
        entity.setDataChangeCreatedBy("zkl");

        Item actual = itemService.save(entity);
        Assert.assertNotNull(actual);
    }
}