package com.example.my.apollo.biz;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Transactional
 * By default Spring will start a new transaction surrounding your test method
 *  and @Before/@After callbacks, rolling back at the end. 
 */
@RunWith(SpringRunner.class)
// @Rollback
@Transactional
@SpringBootTest(classes = BizTestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    
}