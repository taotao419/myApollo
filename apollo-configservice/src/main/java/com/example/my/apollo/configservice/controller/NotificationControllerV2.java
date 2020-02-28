package com.example.my.apollo.configservice.controller;

import com.example.my.apollo.biz.entity.ReleaseMessage;
import com.example.my.apollo.biz.message.ReleaseMessageListener;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications/v2")
public class NotificationControllerV2 implements ReleaseMessageListener {

    @Override
    public void handleMessage(ReleaseMessage message, String channel) {
        // TODO Auto-generated method stub

    }

    
}