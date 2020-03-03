package com.example.my.apollo.configservice.service;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.example.my.apollo.biz.config.BizConfig;
import com.example.my.apollo.biz.entity.ReleaseMessage;
import com.example.my.apollo.biz.message.Topics;
import com.example.my.apollo.biz.repository.ReleaseMessageRepository;
import com.google.common.collect.Lists;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@RunWith(MockitoJUnitRunner.class)
public class ReleaseMessageServiceWithCacheTest {
    private ReleaseMessageServiceWithCache releaseMessageServiceWithCache;

    @Mock
    private ReleaseMessageRepository releaseMessageRepository;

    @Mock
    private BizConfig bizConfig;

    @Before
    public void setUp() throws Exception {
        releaseMessageServiceWithCache = new ReleaseMessageServiceWithCache(releaseMessageRepository, bizConfig);
        releaseMessageServiceWithCache.reset();
        when(bizConfig.releaseMessageCacheScanInterval()).thenReturn(10);
        when(bizConfig.releaseMessageCacheScanIntervalTimeUnit()).thenReturn(TimeUnit.MILLISECONDS);
    }

    @Test
    public void testFindLatestReleaseMessageForMessages() throws Exception {
        ReleaseMessage releaseMessage1 = new ReleaseMessage(0, "msg1");
        ReleaseMessage releaseMessage2 = new ReleaseMessage(1, "msg2");
        ReleaseMessage releaseMessage3 = new ReleaseMessage(2, "msg3");
        List<ReleaseMessage> releaseMessages = Lists.newArrayList(releaseMessage1, releaseMessage2, releaseMessage3);
        when(releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(0l)).thenReturn(releaseMessages);

        releaseMessageServiceWithCache.afterPropertiesSet();

        Set<String> messages = Sets.newSet("msg1", "msg2", "msg3");
        releaseMessageServiceWithCache.findLatestReleaseMessageForMessages(messages);
    }

    @Test
    public void test_ReleaseMessageNoGap() throws Exception {
        ReleaseMessage releaseMessage1 = new ReleaseMessage(0, "msg1");
        ReleaseMessage releaseMessage2 = new ReleaseMessage(1, "msg2");
        ReleaseMessage releaseMessage3 = new ReleaseMessage(2, "msg3");
        List<ReleaseMessage> releaseMessages = Lists.newArrayList(releaseMessage1, releaseMessage2, releaseMessage3);
        when(releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(0l)).thenReturn(releaseMessages);

        ReleaseMessage noGapReleaseMessage = new ReleaseMessage(3, "msg3");
        releaseMessageServiceWithCache.afterPropertiesSet();
        releaseMessageServiceWithCache.handleMessage(noGapReleaseMessage, Topics.APOLLO_RELEASE_TOPIC);
        Set<String> messages = Sets.newSet("msg1", "msg2", "msg3", "msgNA");
        ReleaseMessage actual = releaseMessageServiceWithCache.findLatestReleaseMessageForMessages(messages);

        Assert.assertEquals(3, actual.getId());
    }

    @Test
    public void test_ReleaseMessageWithGap() throws Exception {
        //1st batch
        ReleaseMessage releaseMessage1 = new ReleaseMessage(0, "msg1");
        ReleaseMessage releaseMessage2 = new ReleaseMessage(1, "msg2");
        ReleaseMessage releaseMessage3 = new ReleaseMessage(2, "msg3");
        List<ReleaseMessage> releaseMessages = Lists.newArrayList(releaseMessage1, releaseMessage2, releaseMessage3);
        when(releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(0l)).thenReturn(releaseMessages);
        //2nd batch
        ReleaseMessage releaseMessage4 = new ReleaseMessage(3, "msg4");
        ReleaseMessage releaseMessage5 = new ReleaseMessage(4, "msg5");
        ReleaseMessage releaseMessage6 = new ReleaseMessage(5, "msg6");
        List<ReleaseMessage> releaseMessages2 = Lists.newArrayList(releaseMessage4, releaseMessage5, releaseMessage6);
        when(releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(2l)).thenReturn(releaseMessages2);

        //start threadfactory and wait 5 seconds for loading 2 batches.
        releaseMessageServiceWithCache.afterPropertiesSet();
        Thread.sleep(5000);
        Set<String> messages = Sets.newSet("msg1", "msg2", "msg3", "msg6");
        ReleaseMessage actual = releaseMessageServiceWithCache.findLatestReleaseMessageForMessages(messages);

        Assert.assertEquals(5, actual.getId());
    }
}