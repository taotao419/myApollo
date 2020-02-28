package com.example.my.apollo.biz.repository;

import java.util.Collection;
import java.util.List;

import com.example.my.apollo.biz.entity.ReleaseMessage;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * ReleaseMessageRepository
 */
public interface ReleaseMessageRepository extends PagingAndSortingRepository<ReleaseMessage,Long>{
    List<ReleaseMessage> findFirst500ByIdGreaterThanOrderByIdAsc(Long id);

    ReleaseMessage findTopByOrderByIdDesc();
  
    ReleaseMessage findTopByMessageInOrderByIdDesc(Collection<String> messages);
  
    List<ReleaseMessage> findFirst100ByMessageAndIdLessThanOrderByIdAsc(String message, Long id);
  
    @Query("select message, max(id) as id from ReleaseMessage where message in :messages group by message")
    List<Object[]> findLatestReleaseMessagesGroupByMessages(@Param("messages") Collection<String> messages);
    
}