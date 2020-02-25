package com.example.my.apollo.portal.api;

import com.example.my.apollo.common.dto.AppDTO;
import com.example.my.apollo.core.enums.Env;

import org.springframework.stereotype.Service;

public class AdminServiceAPI {

    @Service
    public static class AppAPI extends API {
        public AppDTO loadApp(Env env, String appId) {
            return restTemplate.get(env, "apps/{appId}", AppDTO.class, appId);
        }

        public AppDTO createApp(Env env, AppDTO app) {
            return restTemplate.post(env, "apps", app, AppDTO.class);
        }

        public void updateApp(Env env, AppDTO app) {
            restTemplate.put(env, "apps/{appId}", app, app.getAppId());
        }

        public void deleteApp(Env env, String appId, String operator) {
            restTemplate.delete(env, "/apps/{appId}?operator={operator}", appId, operator);
        }
    }
}