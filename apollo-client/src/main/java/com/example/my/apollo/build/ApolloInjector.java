package com.example.my.apollo.build;

import com.example.my.apollo.exceptions.ApolloConfigException;
import com.example.my.apollo.internals.Injector;
import com.example.my.apollo.tracer.Tracer;
import com.example.my.foundation.internals.ServiceBootstrap;

/**
 * ApolloInjector
 */
public class ApolloInjector {
    private static volatile Injector s_injector;
    private static final Object lock = new Object();
  
    private static Injector getInjector() {
      if (s_injector == null) {
        synchronized (lock) {
          if (s_injector == null) { //二次判定 + 锁 标准的单例模式
            try {
              s_injector = ServiceBootstrap.loadFirst(Injector.class);
            } catch (Throwable ex) {
              ApolloConfigException exception = new ApolloConfigException("Unable to initialize Apollo Injector!", ex);
              Tracer.logError(exception);
              throw exception;
            }
          }
        }
      }
  
      return s_injector;
    }
  
    public static <T> T getInstance(Class<T> clazz) {
      try {
        return getInjector().getInstance(clazz);
      } catch (Throwable ex) {
        Tracer.logError(ex);
        throw new ApolloConfigException(String.format("Unable to load instance for type %s!", clazz.getName()), ex);
      }
    }
  
    public static <T> T getInstance(Class<T> clazz, String name) {
      try {
        return getInjector().getInstance(clazz, name);
      } catch (Throwable ex) {
        Tracer.logError(ex);
        throw new ApolloConfigException(
            String.format("Unable to load instance for type %s and name %s !", clazz.getName(), name), ex);
      }
    }
    
}