package com.example.my.apollo.biz.repository;

import java.util.List;

import com.example.my.apollo.biz.entity.Commit;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * CommitRepository
 */
public interface CommitRepository extends PagingAndSortingRepository<Commit, Long> {

    List<Commit> findByAppIdAndClusterNameAndNamespaceNameOrderByIdDesc(String appId, String clusterName,
            String namespaceName, Pageable pageable);

    @Modifying
    @Query("update Commit set isdeleted=1,DataChange_LastModifiedBy = ?4 where appId=?1 and clusterName=?2 and namespaceName = ?3")
    int batchDelete(String appId, String clusterName, String namespaceName, String operator);

}