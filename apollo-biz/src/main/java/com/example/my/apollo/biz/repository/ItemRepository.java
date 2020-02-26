package com.example.my.apollo.biz.repository;

import java.util.Date;
import java.util.List;

import com.example.my.apollo.biz.entity.Item;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ItemRepository extends PagingAndSortingRepository<Item,Long>{
    Item findByNamespaceIdAndKey(Long namespaceId, String key);

    List<Item> findByNamespaceIdOrderByLineNumAsc(Long namespaceId);
  
    List<Item> findByNamespaceId(Long namespaceId);
  
    List<Item> findByNamespaceIdAndDataChangeLastModifiedTimeGreaterThan(Long namespaceId, Date date);
  
    Item findFirst1ByNamespaceIdOrderByLineNumDesc(Long namespaceId);
  
    @Modifying
    @Query("update Item set isdeleted=1,DataChange_LastModifiedBy = ?2 where namespaceId = ?1")
    int deleteByNamespaceId(long namespaceId, String operator);
    
}