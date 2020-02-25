package com.example.my.apollo.adminservice;

import com.example.my.apollo.biz.ApolloBizConfig;
import com.example.my.apollo.common.ApolloCommonConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {ApolloCommonConfig.class,
    ApolloBizConfig.class,
    AdminServiceApplication.class})
public class AdminServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class,args);
    }
}