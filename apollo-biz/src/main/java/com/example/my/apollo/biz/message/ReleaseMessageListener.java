package com.example.my.apollo.biz.message;

import com.example.my.apollo.biz.entity.ReleaseMessage;

/**
 * ReleaseMessageListener
 */
public interface ReleaseMessageListener {
    void handleMessage(ReleaseMessage message, String channel);
    
}