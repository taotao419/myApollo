package com.example.my.apollo.portal.repository;

import java.util.List;

import com.example.my.apollo.portal.AbstractUnitTest;
import com.example.my.apollo.portal.entity.po.UserPO;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * UserRepositoryTest
 */
public class UserRepositoryTest extends AbstractUnitTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindFirst20ByEnabled() {
        int enabled = 1;
        List<UserPO> actuals = userRepository.findFirst20ByEnabled(enabled);
        Assert.assertFalse(actuals.isEmpty());
    }

}