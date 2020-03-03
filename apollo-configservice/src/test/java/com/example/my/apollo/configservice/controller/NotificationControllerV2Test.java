package com.example.my.apollo.configservice.controller;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.async.DeferredResult;

import static org.mockito.Mockito.when;

import java.util.List;

import com.example.my.apollo.biz.config.BizConfig;
import com.example.my.apollo.biz.utils.EntityManagerUtil;
import com.example.my.apollo.configservice.service.ReleaseMessageServiceWithCache;
import com.example.my.apollo.configservice.util.NamespaceUtil;
import com.example.my.apollo.configservice.util.WatchKeysUtil;
import com.example.my.apollo.configservice.wrapper.DeferredResultWrapper;
import com.example.my.apollo.core.ConfigConsts;
import com.example.my.apollo.core.dto.ApolloConfigNotification;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@RunWith(MockitoJUnitRunner.class)
public class NotificationControllerV2Test {

    private NotificationControllerV2 controller;
    private String someAppId;
    private String someCluster;
    private String defaultCluster;
    private String defaultNamespace;
    private String somePublicNamespace;
    private String someDataCenter;
    private long someNotificationId;
    private String someClientIp;
    @Mock
    private ReleaseMessageServiceWithCache releaseMessageService;
    @Mock
    private EntityManagerUtil entityManagerUtil;
    @Mock
    private NamespaceUtil namespaceUtil;
    @Mock
    private WatchKeysUtil watchKeysUtil;
    @Mock
    private BizConfig bizConfig;

    private Gson gson;

    private Multimap<String, DeferredResultWrapper> deferredResults;

    @Before
    public void setUp() throws Exception {
        gson = new Gson();
        controller = new NotificationControllerV2(watchKeysUtil, releaseMessageService, entityManagerUtil,
                namespaceUtil, gson, bizConfig);

        when(bizConfig.releaseMessageNotificationBatch()).thenReturn(100);
        when(bizConfig.releaseMessageNotificationBatchIntervalInMilli()).thenReturn(5);

        someAppId = "someAppId";
        someCluster = "someCluster";
        defaultCluster = ConfigConsts.CLUSTER_NAME_DEFAULT;
        defaultNamespace = ConfigConsts.NAMESPACE_APPLICATION;
        somePublicNamespace = "somePublicNamespace";
        someDataCenter = "someDC";
        someNotificationId = 1;
        someClientIp = "someClientIp";

        when(namespaceUtil.filterNamespaceName(defaultNamespace)).thenReturn(defaultNamespace);
        when(namespaceUtil.filterNamespaceName(somePublicNamespace)).thenReturn(somePublicNamespace);
        when(namespaceUtil.normalizeNamespace(someAppId, defaultNamespace)).thenReturn(defaultNamespace);
        when(namespaceUtil.normalizeNamespace(someAppId, somePublicNamespace)).thenReturn(somePublicNamespace);

        deferredResults = (Multimap<String, DeferredResultWrapper>) ReflectionTestUtils.getField(controller,
                "deferredResults");
    }

    @Test
    public void testPollNotificationWithDefaultNamespace() throws Exception {
        String someWatchKey = "someKey";
        String anotherWatchKey = "anotherKey";

        Multimap<String, String> watchKeysMap = assembleMultiMap(defaultNamespace,
                Lists.newArrayList(someWatchKey, anotherWatchKey));

        String notificationAsString = transformApolloConfigNotificationsToString(defaultNamespace, someNotificationId);

        when(watchKeysUtil.assembleAllWatchKeys(someAppId, someCluster, Sets.newHashSet(defaultNamespace),
                someDataCenter)).thenReturn(watchKeysMap);

        DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> deferredResult = controller
                .pollNotification(someAppId, someCluster, notificationAsString, someDataCenter, someClientIp);

        Assert.assertEquals(watchKeysMap.size(), deferredResults.size());

    }
    
    private String transformApolloConfigNotificationsToString(
        String namespace, long notificationId) {
      List<ApolloConfigNotification> notifications =
          Lists.newArrayList(assembleApolloConfigNotification(namespace, notificationId));
      return gson.toJson(notifications);
    }
  
    private String transformApolloConfigNotificationsToString(String namespace, long notificationId,
                                                              String anotherNamespace,
                                                              long anotherNotificationId) {
      List<ApolloConfigNotification> notifications =
          Lists.newArrayList(assembleApolloConfigNotification(namespace, notificationId),
              assembleApolloConfigNotification(anotherNamespace, anotherNotificationId));
      return gson.toJson(notifications);
    }

    private String transformApolloConfigNotificationsToString(String namespace, long notificationId,
            String anotherNamespace, long anotherNotificationId, String yetAnotherNamespace,
            long yetAnotherNotificationId) {
        List<ApolloConfigNotification> notifications = Lists.newArrayList(
                assembleApolloConfigNotification(namespace, notificationId),
                assembleApolloConfigNotification(anotherNamespace, anotherNotificationId),
                assembleApolloConfigNotification(yetAnotherNamespace, yetAnotherNotificationId));
        return gson.toJson(notifications);
    }

    private ApolloConfigNotification assembleApolloConfigNotification(String namespace, long notificationId) {
        ApolloConfigNotification notification = new ApolloConfigNotification(namespace, notificationId);
        return notification;
    }

    private Multimap<String, String> assembleMultiMap(String key, Iterable<String> values) {
        Multimap<String, String> multimap = HashMultimap.create();
        multimap.putAll(key, values);
        return multimap;
    }
}