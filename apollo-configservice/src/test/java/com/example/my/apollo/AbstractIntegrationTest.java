package com.example.my.apollo;


import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConfigTestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    
}