package com.example.my.apollo.configservice.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.example.my.apollo.biz.config.BizConfig;
import com.example.my.apollo.biz.repository.AppNamespaceRepository;
import com.example.my.apollo.common.entity.AppNamespace;
import com.example.my.apollo.configservice.wrapper.CaseInsensitiveMapWrapper;
import com.google.common.collect.Lists;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class AppNamespaceServiceWithCacheTest {
    private AppNamespaceServiceWithCache appNamespaceServiceWithCache;

    @Mock
    private AppNamespaceRepository appNamespaceRepository;

    @Mock
    private BizConfig bizConfig;

    private int scanInterval;
    private TimeUnit scanIntervalTimeUnit;
    // private Comparator<AppNamespace> appNamespaceComparator = (o1, o2) -> (int)
    // (o1.getId() -
    // o2.getId());

    @Before
    public void setUp() throws Exception {
        appNamespaceServiceWithCache = new AppNamespaceServiceWithCache(appNamespaceRepository, bizConfig);

        scanInterval = 50;
        scanIntervalTimeUnit = TimeUnit.MILLISECONDS;
        when(bizConfig.appNamespaceCacheRebuildInterval()).thenReturn(scanInterval);
        when(bizConfig.appNamespaceCacheRebuildIntervalTimeUnit()).thenReturn(scanIntervalTimeUnit);
        when(bizConfig.appNamespaceCacheScanInterval()).thenReturn(scanInterval);
        when(bizConfig.appNamespaceCacheScanIntervalTimeUnit()).thenReturn(scanIntervalTimeUnit);
    }

    @Test
    public void testScanNewAppNamespaces() throws Exception {
        long id=0;
        AppNamespace privateAppNamespace_1 = assembleAppNamespace(1,"SampleApp","application",false);
        
        when(appNamespaceRepository.findFirst500ByIdGreaterThanOrderByIdAsc(id)).thenReturn(Lists.newArrayList(privateAppNamespace_1));
        
        ReflectionTestUtils.invokeMethod(appNamespaceServiceWithCache, "scanNewAppNamespaces");
        Assert.assertEquals(1,appNamespaceServiceWithCache.getAppNamespaceCacheForUT().getSize());
        Assert.assertEquals(0,appNamespaceServiceWithCache.getPublicAppNamespaceCacheForUT().getSize());

        //invoke 2nd time
        id=1;
        AppNamespace privateAppNamespace_3 = assembleAppNamespace(3,"SampleApp-3","application",false);
        AppNamespace publicAppNamespace = assembleAppNamespace(2,"SampleApp","application",true);
        when(appNamespaceRepository.findFirst500ByIdGreaterThanOrderByIdAsc(id)).thenReturn(Lists.newArrayList(privateAppNamespace_3,publicAppNamespace));
        ReflectionTestUtils.invokeMethod(appNamespaceServiceWithCache, "scanNewAppNamespaces");
        
        Assert.assertEquals(2,appNamespaceServiceWithCache.getAppNamespaceCacheForUT().getSize());
        Assert.assertEquals(1,appNamespaceServiceWithCache.getPublicAppNamespaceCacheForUT().getSize());
    }

    @Test
    public void testUpdateAndDeleteCache(){
        long id=0;
        AppNamespace privateAppNamespace_1 = assembleAppNamespace(1,"SampleApp","application",false);
        AppNamespace privateAppNamespace_3 = assembleAppNamespace(3,"SampleApp-3","application",false);
        AppNamespace publicAppNamespace = assembleAppNamespace(2,"SampleApp","application",true);
        
        //init [private1,private3,public]
        when(appNamespaceRepository.findFirst500ByIdGreaterThanOrderByIdAsc(id)).thenReturn(Lists.newArrayList(privateAppNamespace_1,privateAppNamespace_3,publicAppNamespace));
        ReflectionTestUtils.invokeMethod(appNamespaceServiceWithCache, "scanNewAppNamespaces");
        Assert.assertEquals(2,appNamespaceServiceWithCache.getAppNamespaceCacheForUT().getSize());
        Assert.assertEquals(1,appNamespaceServiceWithCache.getPublicAppNamespaceCacheForUT().getSize());

        //Step 2 ,delete one private and update one public [private1,public-update]
        List<Long> toRebuild=Lists.newArrayList(1l,2l,3l);
        publicAppNamespace = assembleAppNamespace(2,"SampleApp-update","application",true);
        when(appNamespaceRepository.findAllById(toRebuild)).thenReturn(Lists.newArrayList(privateAppNamespace_1,publicAppNamespace));
        ReflectionTestUtils.invokeMethod(appNamespaceServiceWithCache, "updateAndDeleteCache"); 
        Assert.assertEquals(1,appNamespaceServiceWithCache.getAppNamespaceCacheForUT().getSize());
        Assert.assertEquals(1,appNamespaceServiceWithCache.getPublicAppNamespaceCacheForUT().getSize());

       AppNamespace actual=  appNamespaceServiceWithCache.findPublicNamespaceByName("application");
       Assert.assertEquals("SampleApp-update", actual.getAppId());
    }

    private AppNamespace assembleAppNamespace(long id, String appId, String name, boolean isPublic) {
        AppNamespace appNamespace = new AppNamespace();
        appNamespace.setId(id);
        appNamespace.setAppId(appId);
        appNamespace.setName(name);
        appNamespace.setPublic(isPublic);
        appNamespace.setDataChangeLastModifiedTime(new Date());
        return appNamespace;
      }
}