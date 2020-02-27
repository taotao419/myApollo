package com.example.my.apollo.portal.component.config;

import java.util.List;

import com.example.my.apollo.core.enums.Env;
import com.google.common.collect.Lists;

import org.springframework.stereotype.Component;

@Component
public class PortalConfig {

    public List<Env> portalSupportedEnvs() {
        List<Env> envs = Lists.newLinkedList();

        envs.add(Env.DEV);
        envs.add(Env.UAT);
        envs.add(Env.PRO);

        return envs;
    }

    public int connectTimeout() {
        return 3000;
    }

    public int readTimeout() {
        return 10000;
    }
}