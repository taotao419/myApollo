package com.example.my.apollo.biz.repository;

import com.example.my.apollo.biz.entity.Instance;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * InstanceRepository
 */
public interface InstanceRepository extends PagingAndSortingRepository<Instance,Long>{

    Instance findByAppIdAndClusterNameAndDataCenterAndIp(String appId, String clusterName, String dataCenter, String ip);
}