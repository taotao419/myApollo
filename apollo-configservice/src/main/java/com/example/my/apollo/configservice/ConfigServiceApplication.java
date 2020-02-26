package com.example.my.apollo.configservice;

import com.example.my.apollo.biz.ApolloBizConfig;
import com.example.my.apollo.common.ApolloCommonConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableEurekaServer
@EnableAspectJAutoProxy
@EnableAutoConfiguration // (exclude = EurekaClientConfigBean.class)
@Configuration
@EnableTransactionManagement
@PropertySource(value = { "classpath:configservice.properties" })
@ComponentScan(basePackageClasses = { ApolloCommonConfig.class, 
    ApolloBizConfig.class, 
    ConfigServiceApplication.class })
public class ConfigServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApplication.class, args);
    }
}