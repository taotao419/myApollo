package com.example.my.apollo.portal.spi.defaultimpl;

import com.example.my.apollo.portal.entity.bo.UserInfo;
import com.example.my.apollo.portal.spi.UserInfoHolder;

import org.springframework.stereotype.Component;

/**
 * DefaultUserInfoHolder
 */
@Component
public class DefaultUserInfoHolder implements UserInfoHolder {

    public DefaultUserInfoHolder() {

    }

    @Override
    public UserInfo getUser() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("apollo");
        return userInfo;
    }

}