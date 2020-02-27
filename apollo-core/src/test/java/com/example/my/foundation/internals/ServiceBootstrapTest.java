package com.example.my.foundation.internals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ServiceConfigurationError;

import com.example.my.apollo.core.spi.MetaServerProvider;
import com.example.my.apollo.core.spi.Ordered;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 */
public class ServiceBootstrapTest {
  @Test
  public void test() {
    Interface1 service = ServiceBootstrap.loadFirst(Interface1.class);
    assertTrue(service instanceof Interface1Impl);
  }

  @Test(expected = IllegalStateException.class)
  public void loadFirstWithNoServiceFileDefined() throws Exception {
    ServiceBootstrap.loadFirst(Interface2.class);
  }

  @Test(expected = IllegalStateException.class)
  public void loadFirstWithServiceFileButNoServiceImpl() throws Exception {
    ServiceBootstrap.loadFirst(Interface3.class);
  }

  @Test(expected = ServiceConfigurationError.class)
  public void loadFirstWithWrongServiceImpl() throws Exception {
    ServiceBootstrap.loadFirst(Interface4.class);
  }

  @Test(expected = ServiceConfigurationError.class)
  public void loadFirstWithServiceImplNotExists() throws Exception {
    ServiceBootstrap.loadFirst(Interface5.class);
  }

  @Test
  public void loadPrimarySuccessfully() {
    Interface6 service = ServiceBootstrap.loadPrimary(Interface6.class);
    assertTrue(service instanceof Interface6Impl1);
  }

  @Test(expected = IllegalStateException.class)
  public void loadPrimaryWithServiceFileButNoServiceImpl() {
    ServiceBootstrap.loadPrimary(Interface7.class);
  }

  /************************* */
  private interface Interface1 {
  }

  public static class Interface1Impl implements Interface1 {
  }

  private interface Interface2 {
  }

  private interface Interface3 {
  }

  private interface Interface4 {
  }

  private interface Interface5 {
  }

  private interface Interface6 extends Ordered {
  }

  public static class Interface6Impl1 implements Interface6 {
    @Override
    public int getOrder() {
      return 1;
    }
  }

  public static class Interface6Impl2 implements Interface6 {
    @Override
    public int getOrder() {
      return 2;
    }
  }

  private interface Interface7 extends Ordered {
  }
}