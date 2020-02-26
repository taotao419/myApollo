package com.example.my.apollo.portal.repository;

import java.util.List;

import com.example.my.apollo.portal.entity.po.UserPO;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * UserRepository
 */
public interface UserRepository extends PagingAndSortingRepository<UserPO,Long>{

    List<UserPO> findFirst20ByEnabled(int enabled);

    List<UserPO> findByUsernameLikeAndEnabled(String username, int enabled);
  
    UserPO findByUsername(String username);
  
    List<UserPO> findByUsernameIn(List<String> userNames);
    
}