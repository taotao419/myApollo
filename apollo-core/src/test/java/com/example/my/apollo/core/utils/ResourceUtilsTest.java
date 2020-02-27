package com.example.my.apollo.core.utils;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public class ResourceUtilsTest {

    @Test
    public void test() {
        String configPath = "demo.properties";
        Properties actual = ResourceUtils.readConfigFile(configPath, null);
        Assert.assertNotNull(actual);
        Assert.assertEquals(1, actual.size());
        Assert.assertTrue(actual.containsKey("some.app.flag"));
        Assert.assertEquals( "false", actual.get("some.app.flag"));
    }
}