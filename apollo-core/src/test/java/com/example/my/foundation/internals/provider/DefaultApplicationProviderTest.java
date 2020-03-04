package com.example.my.foundation.internals.provider;

import org.junit.Assert;
import org.junit.Test;

/**
 * DefaultApplicationProviderTest
 */
public class DefaultApplicationProviderTest {
    private DefaultApplicationProvider defaultApplicationProvider = new DefaultApplicationProvider();
    String PREDEFINED_APP_ID = "110402";

    @Test
    public void test() {
        defaultApplicationProvider.initialize();
        Assert.assertEquals(PREDEFINED_APP_ID, defaultApplicationProvider.getAppId());
    }
}