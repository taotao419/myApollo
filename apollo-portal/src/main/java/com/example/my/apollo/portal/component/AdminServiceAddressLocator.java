package com.example.my.apollo.portal.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.example.my.apollo.core.MetaDomainConsts;
import com.example.my.apollo.core.dto.ServiceDTO;
import com.example.my.apollo.core.enums.Env;
import com.example.my.apollo.core.utils.ApolloThreadFactory;
import com.example.my.apollo.portal.component.config.PortalConfig;
import com.example.my.apollo.tracer.Tracer;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

@Component
public class AdminServiceAddressLocator {

    private static final long NORMAL_REFRESH_INTERVAL = 5 * 60 * 1000;
    private static final long OFFLINE_REFRESH_INTERVAL = 10 * 1000;
    private static final int RETRY_TIMES = 3;
    private static final String ADMIN_SERVICE_URL_PATH = "/services/admin";
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceAddressLocator.class);

    private ScheduledExecutorService refreshServiceAddressService;
    private RestTemplate restTemplate;
    private List<Env> allEnvs;
    private Map<Env, List<ServiceDTO>> cache = new ConcurrentHashMap<>();

    private final PortalConfig portalConfig;
    private final RestTemplateFactory restTemplateFactory;

    public AdminServiceAddressLocator(final HttpMessageConverters httpMessageConverters,
            final PortalConfig portalConfig, final RestTemplateFactory restTemplateFactory) {
        this.portalConfig = portalConfig;
        this.restTemplateFactory = restTemplateFactory;
    }

    @PostConstruct
    public void init() {
        allEnvs = portalConfig.portalSupportedEnvs(); // portalSettings.getAllEnvs();

        // init restTemplate
        restTemplate = restTemplateFactory.getObject();

        refreshServiceAddressService = Executors.newScheduledThreadPool(1,
                ApolloThreadFactory.create("ServiceLocator", true));

        refreshServiceAddressService.schedule(new RefreshAdminServerAddressTask(), 1, TimeUnit.MILLISECONDS);
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

    // maintain admin server address
    private class RefreshAdminServerAddressTask implements Runnable {

        @Override
        public void run() {
            boolean refreshSuccess = true;
            // refresh fail if get any env address fail
            for (Env env : allEnvs) {
                boolean currentEnvRefreshResult = refreshServerAddressCache(env);
                refreshSuccess = refreshSuccess && currentEnvRefreshResult;
            }

            if (refreshSuccess) {
                refreshServiceAddressService.schedule(new RefreshAdminServerAddressTask(), NORMAL_REFRESH_INTERVAL,
                        TimeUnit.MILLISECONDS);
            } else {
                refreshServiceAddressService.schedule(new RefreshAdminServerAddressTask(), OFFLINE_REFRESH_INTERVAL,
                        TimeUnit.MILLISECONDS);
            }
        }
    }

    private boolean refreshServerAddressCache(Env env) {

        for (int i = 0; i < RETRY_TIMES; i++) {

            try {
                ServiceDTO[] services = getAdminServerAddress(env);
                if (services == null || services.length == 0) {
                    continue;
                }
                cache.put(env, Arrays.asList(services));
                return true;
            } catch (Throwable e) {
                logger.error(String.format(
                        "Get admin server address from meta server failed. env: %s, meta server address:%s", env,
                        MetaDomainConsts.getDomain(env)), e);
                Tracer.logError(String.format(
                        "Get admin server address from meta server failed. env: %s, meta server address:%s", env,
                        MetaDomainConsts.getDomain(env)), e);
            }
        }
        return false;
    }

    private ServiceDTO[] getAdminServerAddress(Env env) {
        String domainName = MetaDomainConsts.getDomain(env);
        String url = domainName + ADMIN_SERVICE_URL_PATH;
        return restTemplate.getForObject(url, ServiceDTO[].class);
    }
}