package com.example.my.apollo.biz.service;

import java.util.List;

import com.example.my.apollo.biz.AbstractIntegrationTest;
import com.example.my.apollo.biz.entity.Commit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.junit.Assert;

/**
 * CommitServiceTest
 */
public class CommitServiceTest extends AbstractIntegrationTest {
    @Autowired
    private CommitService commitService;

    @Test
    @Rollback(false)
    public void testSave() {
        Commit commit = new Commit();
        commit.setAppId("unitTestApp-0226-1");
        commit.setClusterName("default");
        commit.setChangeSets("{changeSets}");
        commit.setNamespaceName("application-ut");
        commit.setComment("comment-ut");
        commit.setDataChangeCreatedBy("zkl");

        commitService.save(commit);
    }

    @Test
    public void testFind() {
        String appId = "unitTestApp-0226-1";
        String clusterName = "default";
        String namespaceName = "application-ut";

        List<Commit> actuals = commitService.find(appId, clusterName, namespaceName, PageRequest.of(0, 10));
        Assert.assertFalse(actuals.isEmpty());
    }

    @Test
    @Rollback(false)
    public void testBatchDelete() {
        String appId = "unitTestApp-0226-1";
        String clusterName = "default";
        String namespaceName = "application-ut";
        String operator = "zkl";

        int actual = commitService.batchDelete(appId, clusterName, namespaceName, operator);
    }

}