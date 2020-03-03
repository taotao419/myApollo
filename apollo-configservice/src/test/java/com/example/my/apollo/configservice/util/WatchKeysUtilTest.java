package com.example.my.apollo.configservice.util;

import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Set;

import com.example.my.apollo.common.entity.AppNamespace;
import com.example.my.apollo.configservice.service.AppNamespaceServiceWithCache;
import com.example.my.apollo.core.ConfigConsts;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * WatchKeysUtilTest
 */
@RunWith(MockitoJUnitRunner.class)
public class WatchKeysUtilTest {
    @Mock
    private AppNamespaceServiceWithCache appNamespaceService;

    private WatchKeysUtil watchKeysUtil;

    @Before
    public void setUp() throws Exception {
        watchKeysUtil = new WatchKeysUtil(appNamespaceService);
    }

    @Test
    public void testAssembleAllWatchKeysWithOneNamespaceAndDefaultCluster() throws Exception {
        AppNamespace appNamespace = new AppNamespace();
        appNamespace.setAppId("appId");
        appNamespace.setName("application");

        when(appNamespaceService.findByAppIdAndNamespaces("appId", Sets.newHashSet("application")))
                .thenReturn(Lists.newArrayList(appNamespace));

        Set<String> watchKeys = watchKeysUtil.assembleAllWatchKeys("appId", ConfigConsts.CLUSTER_NAME_DEFAULT,
                "application", null);

        Set<String> clusters = Sets.newHashSet(ConfigConsts.CLUSTER_NAME_DEFAULT);

        Assert.assertEquals(clusters.size(), watchKeys.size());
        assertWatchKeys("appId", clusters, "application", watchKeys);
    }

    @Test
    public void testAssembleAllWatchKeysWithOneNamespaceAndSomeDC() throws Exception {
        AppNamespace appNamespace = new AppNamespace();
        appNamespace.setAppId("appId");
        appNamespace.setName("application");

        when(appNamespaceService.findByAppIdAndNamespaces("appId", Sets.newHashSet("application")))
                .thenReturn(Lists.newArrayList(appNamespace));

        // "appId+default+appliation" , ""appId+DC-1+appliation""
        Set<String> watchKeys = watchKeysUtil.assembleAllWatchKeys("appId", "DC-1", "application", null);

        Set<String> clusters = Sets.newHashSet(ConfigConsts.CLUSTER_NAME_DEFAULT, "DC-1");

        Assert.assertEquals(clusters.size(), watchKeys.size());
        assertWatchKeys("appId", clusters, "application", watchKeys);
    }

    @Test
    public void testAssembleAllWatchKeysWithMultipleNamespaces() throws Exception {
        AppNamespace appNamespace1 = new AppNamespace();
        appNamespace1.setAppId("appId");
        appNamespace1.setName("application");

        AppNamespace appNamespace2 = new AppNamespace();
        appNamespace2.setAppId("appId");
        appNamespace2.setName("demo.example.app");

        when(appNamespaceService.findByAppIdAndNamespaces("appId", Sets.newHashSet("application", "demo.example.app")))
                .thenReturn(Lists.newArrayList(appNamespace1, appNamespace2));

        Multimap<String, String> watchKeysMap = watchKeysUtil.assembleAllWatchKeys("appId", "cluster-1",
                Sets.newHashSet("application", "demo.example.app"), "dataCenter-1");

        Set<String> clusters = Sets.newHashSet(ConfigConsts.CLUSTER_NAME_DEFAULT, "cluster-1", "dataCenter-1");

        // Result : "application" :
        // ["appId+default+application"|"appId+dataCenter-1+application"|"appId+cluster-1+application"]
        // "demo.example.app" :
        // ["appId+default+demo.example.app"|"appId+dataCenter-1+demo.example.app"|"appId+cluster-1+demo.example.app"]
        Assert.assertEquals(clusters.size() * 2, watchKeysMap.size());
        assertWatchKeys("appId", clusters, "application", watchKeysMap.get("application"));
        assertWatchKeys("appId", clusters, "demo.example.app", watchKeysMap.get("demo.example.app"));
    }

    @Test
    public void testAssembleAllWatchKeysWithPrivateAndPublicNamespaces() throws Exception {

        AppNamespace appNamespace1 = new AppNamespace();
        appNamespace1.setAppId("appId");
        appNamespace1.setName("application");

        AppNamespace appNamespace2 = new AppNamespace();
        appNamespace2.setAppId("appId");
        appNamespace2.setName("demo.example.app");

        AppNamespace publicNamespace = new AppNamespace();
        publicNamespace.setAppId("pub-Id");
        publicNamespace.setName("global.example.app");
        publicNamespace.setPublic(true);

        when(appNamespaceService.findByAppIdAndNamespaces("appId",
                Sets.newHashSet("application", "demo.example.app", "global.example.app")))
                        .thenReturn(Lists.newArrayList(appNamespace1, appNamespace2));
        when(appNamespaceService.findPublicNamespacesByNames(Sets.newHashSet("global.example.app")))
                .thenReturn(Lists.newArrayList(publicNamespace));

        Multimap<String, String> watchKeysMap = watchKeysUtil.assembleAllWatchKeys("appId", "cluster-1",
                Sets.newHashSet("application", "demo.example.app", "global.example.app"), "dataCenter-1");

        Set<String> clusters = Sets.newHashSet(ConfigConsts.CLUSTER_NAME_DEFAULT, "cluster-1", "dataCenter-1");

        Assert.assertEquals(clusters.size() * 4, watchKeysMap.size());
        // Result : "application" :
        // ["appId+default+application"|"appId+dataCenter-1+application"|"appId+cluster-1+application"]
        // "demo.example.app" :
        // ["appId+default+demo.example.app"|"appId+dataCenter-1+demo.example.app"|"appId+cluster-1+demo.example.app"]
        // "global.example.app"
        // ["pub-Id+dataCenter-1+global.example.app"|"appId+dataCenter-1+global.example.app"|"pub-Id+cluster-1+global.example.app"|"pub-Id+default+global.example.app"|"appId+cluster-1+global.example.app"|"appId+default+global.example.app"]
        assertWatchKeys("appId", clusters, "application", watchKeysMap.get("application"));
        assertWatchKeys("appId", clusters, "demo.example.app", watchKeysMap.get("demo.example.app"));
        assertWatchKeys("appId", clusters, "global.example.app", watchKeysMap.get("global.example.app"));
        assertWatchKeys("pub-Id", clusters, "global.example.app", watchKeysMap.get("global.example.app"));
    }

    @Test
    public void testAssembleWatchKeysForNoAppIdPlaceHolder() throws Exception {
        Multimap<String, String> watchKeysMap = watchKeysUtil.assembleAllWatchKeys(ConfigConsts.NO_APPID_PLACEHOLDER,
                "cluster-1", Sets.newHashSet("application", "demo.example.app"), "dataCenter-1");

        Assert.assertTrue(watchKeysMap.isEmpty());
    }

    private void assertWatchKeys(String appId, Set<String> clusters, String namespaceName,
            Collection<String> watchedKeys) {
        for (String cluster : clusters) {
            String key = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR).join(appId, cluster, namespaceName);
            Assert.assertTrue(watchedKeys.contains(key));
        }
    }
}