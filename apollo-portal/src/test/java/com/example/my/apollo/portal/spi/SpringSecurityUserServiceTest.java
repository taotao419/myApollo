package com.example.my.apollo.portal.spi;

import com.example.my.apollo.portal.AbstractUnitTest;
import com.example.my.apollo.portal.entity.po.UserPO;
import com.example.my.apollo.portal.spi.springsecurity.SpringSecurityUserService;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SpringSecurityUserServiceTest extends AbstractUnitTest {

    @Autowired
    private UserService userService;

    @Test
    public void testCreateOrUpdate() {
        UserPO user = new UserPO();
        user.setId(2);
        user.setUsername("zkl");
        user.setPassword("password@1");
        user.setEmail("zkl@msn.com");
        ((SpringSecurityUserService) userService).createOrUpdate(user);
    }

}