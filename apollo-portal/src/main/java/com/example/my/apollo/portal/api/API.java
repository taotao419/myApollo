package com.example.my.apollo.portal.api;

import com.example.my.apollo.portal.component.RetryableRestTemplate;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class API {
    @Autowired
    protected RetryableRestTemplate restTemplate;

}