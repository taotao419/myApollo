package com.example.my.apollo.biz;

import com.example.my.apollo.common.ApolloCommonConfig;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * ComponentScan : spring就会去自动扫描base-package对应的路径或者该路径的子包下面的
 * 带有@Service,@Component,@Repository,@Controller注解的java文件
 * base-package: 指定扫描路径。
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {ApolloCommonConfig.class, ApolloBizConfig.class})
public class BizTestConfiguration {

    
}