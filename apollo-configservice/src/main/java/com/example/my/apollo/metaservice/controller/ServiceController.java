package com.example.my.apollo.metaservice.controller;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.example.my.apollo.core.dto.ServiceDTO;
import com.example.my.apollo.metaservice.service.DiscoveryService;
import com.netflix.appinfo.InstanceInfo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services")
public class ServiceController {
    private final DiscoveryService discoveryService;

    private static Function<InstanceInfo, ServiceDTO> instanceInfoToServiceDTOFunc = instance -> {
        ServiceDTO service = new ServiceDTO();
        service.setAppName(instance.getAppName());
        service.setInstanceId(instance.getInstanceId());
        service.setHomepageUrl(instance.getHomePageUrl());
        return service;
    };

    public ServiceController(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    /**
     * 实际上，services/meta 暂时是不可用的，获取不到实例，因为 Meta Service 目前内嵌在 Config Service 中。
     */
    @RequestMapping("/meta")
    public List<ServiceDTO> getMetaService() {
        List<InstanceInfo> instances = discoveryService.getMetaServiceInstances();
        List<ServiceDTO> result = instances.stream().map(instanceInfoToServiceDTOFunc).collect(Collectors.toList());

        return result;
    }

    @RequestMapping("/config")
    public List<ServiceDTO> getConfigService(@RequestParam(value = "appId", defaultValue = "") String appId,
            @RequestParam(value = "ip", required = false) String clientIp) {
        List<InstanceInfo> instances = discoveryService.getConfigServiceInstances();
        List<ServiceDTO> result = instances.stream().map(instanceInfoToServiceDTOFunc).collect(Collectors.toList());
        return result;
    }

    @RequestMapping("/admin")
    public List<ServiceDTO> getAdminService() {
        List<InstanceInfo> instances = discoveryService.getAdminServiceInstances();
        List<ServiceDTO> result = instances.stream().map(instanceInfoToServiceDTOFunc).collect(Collectors.toList());
        return result;
    }
}