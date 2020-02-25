package com.example.my.apollo.portal.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import com.example.my.apollo.core.dto.ServiceDTO;
import com.example.my.apollo.core.enums.Env;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class AdminServiceAddressLocator {
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceAddressLocator.class);
    private Map<Env, List<ServiceDTO>> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // Mock env
        List<ServiceDTO> services=new ArrayList<ServiceDTO>();
        ServiceDTO service=new ServiceDTO();
        service.setAppName("DEV AdminConfig");
        service.setHomepageUrl("http://localhost:8085");
        service.setInstanceId("instance-1");
        services.add(service);

        cache.put(Env.DEV, services);
    }

    public List<ServiceDTO> getServiceList(Env env) {
        List<ServiceDTO> services = cache.get(env);
        if (CollectionUtils.isEmpty(services)) {
            return Collections.emptyList();
        }
        List<ServiceDTO> randomConfigServices = Lists.newArrayList(services);
        Collections.shuffle(randomConfigServices);
        return randomConfigServices;
    }
}