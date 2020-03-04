package com.example.my.foundation.internals.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * DefaultServerProviderTest
 */
public class DefaultServerProviderTest {

    private DefaultServerProvider defaultServerProvider;

    @Before
    public void setUp() {
        cleanUp();
        defaultServerProvider = new DefaultServerProvider();
    }

    private void cleanUp() {
        System.clearProperty("env");
        System.clearProperty("idc");
    }

    @Test
    public void testEnvWithSystemProperty() throws Exception {
        String someEnv = "someEnv";
        String someDc = "someDc";
        System.setProperty("env", someEnv);
        System.setProperty("idc", someDc);

        defaultServerProvider.initialize(null);

        assertEquals(someEnv, defaultServerProvider.getEnvType());
        assertEquals(someDc, defaultServerProvider.getDataCenter());
    }

    @Test
    public void testWithPropertiesStream() throws FileNotFoundException, IOException {
      File baseDir=new File("src/test/resources/properties");
      File serverProperties=new File(baseDir,"server.properties");
      defaultServerProvider.initialize(new FileInputStream(serverProperties));

      assertEquals("SHAJQ", defaultServerProvider.getDataCenter());
      assertTrue(defaultServerProvider.isEnvTypeSet());
      assertEquals("DEV", defaultServerProvider.getEnvType());
  }

  @Test
  public void testWithPropertiesStreamAndEnvFromSystemProperty() throws Exception {
    String prodEnv = "pro";
    System.setProperty("env", prodEnv);

    File baseDir = new File("src/test/resources/properties");
    File serverProperties = new File(baseDir, "server.properties");
    defaultServerProvider.initialize(new FileInputStream(serverProperties));

    String predefinedDataCenter = "SHAJQ";

    assertEquals(predefinedDataCenter, defaultServerProvider.getDataCenter());
    assertTrue(defaultServerProvider.isEnvTypeSet());
    assertEquals(prodEnv, defaultServerProvider.getEnvType());
  }
}