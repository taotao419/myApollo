package com.example.my.apollo.biz.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.example.my.apollo.biz.AbstractIntegrationTest;
import com.example.my.apollo.biz.entity.ReleaseMessage;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ReleaseMessageServiceTest
 */
public class ReleaseMessageServiceTest extends AbstractIntegrationTest {
    @Autowired
    private ReleaseMessageService releaseMessageService;

    @Test
    public void testFindLatestReleaseMessageForMessages() {
        Collection<String> messages = new ArrayList<String>();
        messages.add("Demo Message");
        ReleaseMessage releaseMessage = releaseMessageService.findLatestReleaseMessageForMessages(messages);
        Assert.assertNotNull(releaseMessage);
        Assert.assertEquals(1, releaseMessage.getId());
    }

    @Test
    public void testFindLatestReleaseMessagesGroupByMessages() {
        Collection<String> messages = new ArrayList<String>();
        messages.add("Demo Message");
        messages.add("Demo Message 3");

        List<ReleaseMessage> actuals = releaseMessageService.findLatestReleaseMessagesGroupByMessages(messages);
        
        Assert.assertFalse(actuals.isEmpty());
        Assert.assertEquals(2, actuals.size());
    }
}