package com.example.my.apollo.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EntityScan("com.example.my.apollo.common.entity")
public class StartupApp 
{
    public static void main( String[] args )
    {
        SpringApplication.run(StartupApp.class, args);
    }
}
