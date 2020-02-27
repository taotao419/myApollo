package com.example.my.apollo.core;

import java.util.List;

import com.example.my.apollo.core.enums.Env;
import com.example.my.apollo.core.internals.LegacyMetaServerProvider;
import com.example.my.apollo.core.spi.MetaServerProvider;

import org.assertj.core.util.Strings;
import org.junit.Assert;
import org.junit.Test;

/**
 * MetaDomainConstsTest
 */
public class MetaDomainConstsTest {

    @Test
    public void testInitMetaServerProviders() {
        List<MetaServerProvider> actuals = MetaDomainConsts.initMetaServerProvidersForUT();

        Assert.assertEquals(2, actuals.size());
        MetaServerProvider first = actuals.get(0);
        Assert.assertTrue(first instanceof LegacyMetaServerProvider);
    }

    @Test
    public void testGetDomain() {
        String domain = MetaDomainConsts.getDomain(Env.DEV);
        Assert.assertFalse(Strings.isNullOrEmpty(domain));
    }
}