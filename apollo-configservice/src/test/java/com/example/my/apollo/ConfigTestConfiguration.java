package com.example.my.apollo;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * ConfigTestConfiguration
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {ConfigTestConfiguration.class})
public class ConfigTestConfiguration {

    
}