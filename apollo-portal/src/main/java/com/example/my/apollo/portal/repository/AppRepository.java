
package com.example.my.apollo.portal.repository;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import com.example.my.apollo.common.entity.App;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AppRepository extends PagingAndSortingRepository<App, Long> {

    App findByAppId(String appId);

    List<App> findByOwnerName(String ownerName, Pageable page);
  
    List<App> findByAppIdIn(Set<String> appIds);
  
    List<App> findByAppIdIn(Set<String> appIds, Pageable pageable);
  
    Page<App> findByAppIdContainingOrNameContaining(String appId, String name, Pageable pageable);
  
    @Modifying
    @Transactional
    @Query("UPDATE App SET IsDeleted=1,DataChange_LastModifiedBy = ?2 WHERE AppId=?1")
    int deleteApp(String appId, String operator);

    //******Created just for testing *******/
    App findByOrgName(String orgName);
  }