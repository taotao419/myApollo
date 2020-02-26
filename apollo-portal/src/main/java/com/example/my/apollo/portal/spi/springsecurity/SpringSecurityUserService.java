package com.example.my.apollo.portal.spi.springsecurity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import com.example.my.apollo.core.utils.StringUtils;
import com.example.my.apollo.portal.entity.bo.UserInfo;
import com.example.my.apollo.portal.entity.po.UserPO;
import com.example.my.apollo.portal.repository.UserRepository;
import com.example.my.apollo.portal.spi.UserService;
import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.util.CollectionUtils;

public class SpringSecurityUserService implements UserService {
    //在Spring Security中, 对密码的加密都是由PasswordEncoder来完成的.
    //现在推荐spring使用的BCrypt BCryptPasswordEncoder
    private PasswordEncoder encoder=new BCryptPasswordEncoder();
    private List<GrantedAuthority> authorities;

    @Autowired
    private JdbcUserDetailsManager userDetailsManager;
    @Autowired
    private UserRepository userRepository;
    
    @PostConstruct
    public void init() {
      authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority("ROLE_user"));
    }
    
    @Transactional
    public void createOrUpdate(UserPO user) {
      String username = user.getUsername();
  
      User userDetails = new User(username, encoder.encode(user.getPassword()), authorities);
  
      if (userDetailsManager.userExists(username)) {
        userDetailsManager.updateUser(userDetails);
      } else {
        userDetailsManager.createUser(userDetails);
      }
  
      UserPO managedUser = userRepository.findByUsername(username);
      managedUser.setEmail(user.getEmail());
  
      userRepository.save(managedUser);
    }
    
    @Override
    public List<UserInfo> searchUsers(String keyword, int offset, int limit) {
        List<UserPO> users;
        if (StringUtils.isEmpty(keyword)) {
          users = userRepository.findFirst20ByEnabled(1);
        } else {
          users = userRepository.findByUsernameLikeAndEnabled("%" + keyword + "%", 1);
        }
    
        List<UserInfo> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(users)) {
          return result;
        }
    
        result.addAll(users.stream().map(UserPO::toUserInfo).collect(Collectors.toList()));
    
        return result;
    }

    @Override
    public UserInfo findByUserId(String userId) {
        UserPO userPO = userRepository.findByUsername(userId);
        return userPO == null ? null : userPO.toUserInfo();
    }

    @Override
    public List<UserInfo> findByUserIds(List<String> userIds) {
        List<UserPO> users = userRepository.findByUsernameIn(userIds);

        if (CollectionUtils.isEmpty(users)) {
          return Collections.emptyList();
        }
    
        List<UserInfo> result = Lists.newArrayList();
        // UserPO -> UserInfo
        result.addAll(users.stream().map(UserPO::toUserInfo).collect(Collectors.toList()));
    
        return result;
    }

    
}