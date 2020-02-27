package com.example.my.foundation.internals;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import com.example.my.apollo.core.spi.Ordered;
import com.google.common.collect.Lists;

/**
 * 对于java spi大部分开发者都不会陌生，我们使用的第三方框架的配置如:jdbc、日志、spring、微服务框架等。spi全称：Service Provider Interface,实现了模块间的解耦，实现可拔插。
  java spi的约束如下：

  在META-INF/services/目录中创建以接口全限定名命名的文件该文件内容为Api具体实现类的全限定名
  使用ServiceLoader类动态加载META-INF中的实现类
  如SPI的实现类为Jar则需要放在主程序classPath中
  Api具体实现类必须有一个不带参数的构造方法
 * 
 * 创建一个接口文件
   在resources资源目录下创建META-INF/services文件夹
   在services文件夹中创建文件，以接口全名命名
   创建接口实现类
 */
public class ServiceBootstrap {
    public static <S> S loadFirst(Class<S> clazz) {
        Iterator<S> iterator = loadAll(clazz);
        if (!iterator.hasNext()) {
          throw new IllegalStateException(String.format(
              "No implementation defined in /META-INF/services/%s, please check whether the file exists and has the right implementation class!",
              clazz.getName()));
        }
        return iterator.next();
      }
    
      public static <S> Iterator<S> loadAll(Class<S> clazz) {
        ServiceLoader<S> loader = ServiceLoader.load(clazz);
    
        return loader.iterator();
      }
    
      public static <S extends Ordered> List<S> loadAllOrdered(Class<S> clazz) {
        Iterator<S> iterator = loadAll(clazz);
    
        if (!iterator.hasNext()) {
          throw new IllegalStateException(String.format(
              "No implementation defined in /META-INF/services/%s, please check whether the file exists and has the right implementation class!",
              clazz.getName()));
        }
    
        List<S> candidates = Lists.newArrayList(iterator);
        Collections.sort(candidates, new Comparator<S>() {
          @Override
          public int compare(S o1, S o2) {
            // the smaller order has higher priority
            return Integer.compare(o1.getOrder(), o2.getOrder());
          }
        });
    
        return candidates;
      }
    
      public static <S extends Ordered> S loadPrimary(Class<S> clazz) {
        List<S> candidates = loadAllOrdered(clazz);
    
        return candidates.get(0);
      }
    
}