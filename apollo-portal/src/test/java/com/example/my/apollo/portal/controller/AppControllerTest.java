package com.example.my.apollo.portal.controller;

import com.example.my.apollo.common.entity.App;
import com.example.my.apollo.portal.AbstractControllerTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * AppControllerTest
 */
public class AppControllerTest extends AbstractControllerTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void findAppsTest() throws Exception {
        String uri = "/apps?appIds=SampleApp";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        App[] apps = super.mapFromJson(content, App[].class);
        Assert.assertTrue(apps.length > 0);
    }

}