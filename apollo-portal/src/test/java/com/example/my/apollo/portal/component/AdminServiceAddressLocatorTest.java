package com.example.my.apollo.portal.component;

import java.util.List;

import com.example.my.apollo.core.dto.ServiceDTO;
import com.example.my.apollo.core.enums.Env;
import com.example.my.apollo.portal.AbstractUnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminServiceAddressLocatorTest extends AbstractUnitTest {

    @Autowired
    private AdminServiceAddressLocator adminServiceAddressLocator;

    @Test
    public void testGetServiceList() {
        Env env = Env.DEV;
        List<ServiceDTO> actual = adminServiceAddressLocator.getServiceList(env);

        Assert.assertFalse( actual.isEmpty());;
    }

}