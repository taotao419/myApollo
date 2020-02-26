package com.example.my.apollo.portal.spi;

import java.util.List;

import com.example.my.apollo.portal.entity.bo.UserInfo;

/**
 * UserService
 */
public interface UserService {
    List<UserInfo> searchUsers(String keyword, int offset, int limit);

    UserInfo findByUserId(String userId);
  
    List<UserInfo> findByUserIds(List<String> userIds);
    
}