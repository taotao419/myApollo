package com.example.my.apollo.core.internals;

import com.example.my.apollo.core.enums.Env;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
/**
 * LegacyMetaServerProviderTest
 */
public class LegacyMetaServerProviderTest {

    @Test
    public void testInitialize(){
        LegacyMetaServerProvider provider=new LegacyMetaServerProvider();
        provider.initializeForUT();
    }

    @Test
    public void testFromPropertyFile() {
      LegacyMetaServerProvider legacyMetaServerProvider = new LegacyMetaServerProvider();
      assertEquals("http://localhost:8080", legacyMetaServerProvider.getMetaServerAddress(Env.LOCAL));
      assertEquals("http://dev:8080", legacyMetaServerProvider.getMetaServerAddress(Env.DEV));
      assertEquals(null, legacyMetaServerProvider.getMetaServerAddress(Env.PRO));
    }
  
}