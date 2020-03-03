package com.example.my.apollo.configservice.util;

import static org.mockito.Mockito.*;

import com.example.my.apollo.common.entity.AppNamespace;
import com.example.my.apollo.configservice.service.AppNamespaceServiceWithCache;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@RunWith(MockitoJUnitRunner.class)
public class NamespaceUtilTest {
    private NamespaceUtil namespaceUtil;

    @Mock
    private AppNamespaceServiceWithCache appNamespaceServiceWithCache;

    @Before
    public void setUp() {
        namespaceUtil = new NamespaceUtil(appNamespaceServiceWithCache);
    }

    @Test
    public void testFilterNamespaceName() {
        String name = "a.xml";
        String actual = namespaceUtil.filterNamespaceName(name);
        Assert.assertEquals("a.xml", actual);

        // Case 2 extension name is properties
        String name1 = "b.PropErtIes";
        String actual1 = namespaceUtil.filterNamespaceName(name1);
        Assert.assertEquals("b", actual1);
    }

    @Test
    public void testNormalizeNamespaceWithPrivateNamespace() throws Exception {
        String someAppId = "someAppId";
        String someNamespaceName = "someNamespaceName";
        String someNormalizedNamespaceName = "someNormalizedNamespaceName";
        AppNamespace someAppNamespace = mock(AppNamespace.class);

        when(someAppNamespace.getName()).thenReturn(someNormalizedNamespaceName);
        when(appNamespaceServiceWithCache.findByAppIdAndNamespace(someAppId, someNamespaceName))
                .thenReturn(someAppNamespace);

        Assert.assertEquals(someNormalizedNamespaceName,
                namespaceUtil.normalizeNamespace(someAppId, someNamespaceName));

        verify(appNamespaceServiceWithCache, times(1)).findByAppIdAndNamespace(someAppId, someNamespaceName);
        verify(appNamespaceServiceWithCache, never()).findPublicNamespaceByName(someNamespaceName);
    }
}