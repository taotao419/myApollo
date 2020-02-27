package com.example.my.apollo.core.utils;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

/**
 * ClassLoaderUtilTest
 */
public class ClassLoaderUtilTest {

    @Test
    public void testLoaderGetResource() {
        //文件创建在 test/resources/application.properties
        //在单元测试中此文件生成为 ~/apollo/apollo-core/target/test-classes/application.properties
        String fileName = "application.properties";
        URL url = ClassLoaderUtil.getLoader().getResource(fileName);
        Assert.assertNotNull(url);
    }
}