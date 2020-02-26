package com.example.my.apollo.biz.service;

import java.util.List;

import javax.transaction.Transactional;

import com.example.my.apollo.biz.entity.Commit;
import com.example.my.apollo.biz.repository.CommitRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommitService {
    private final CommitRepository commitRepository;

    public CommitService(final CommitRepository commitRepository) {
        this.commitRepository = commitRepository;
    }

    @Transactional
    public Commit save(Commit commit) {
        commit.setId(0);// protection
        return commitRepository.save(commit);
    }

    public List<Commit> find(String appId, String clusterName, String namespaceName, Pageable page) {
        return commitRepository.findByAppIdAndClusterNameAndNamespaceNameOrderByIdDesc(appId, clusterName,
                namespaceName, page);
    }

    @Transactional
    public int batchDelete(String appId, String clusterName, String namespaceName, String operator) {
        return commitRepository.batchDelete(appId, clusterName, namespaceName, operator);
    }
}