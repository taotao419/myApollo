package com.example.my.apollo.portal.controller;

import java.util.List;

import com.example.my.apollo.common.dto.PageDTO;
import com.example.my.apollo.common.entity.App;
import com.example.my.apollo.core.utils.StringUtils;
import com.example.my.apollo.portal.service.AppService;
import com.google.common.collect.Sets;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apps")
public class AppController {
    private final AppService appService;

    public AppController(final AppService appService) {
        this.appService = appService;
    }

    @GetMapping
    public List<App> findApps(@RequestParam(value = "appIds", required = false) String appIds) {
      if (StringUtils.isEmpty(appIds)) {
        return appService.findAll();
      } else {
        return appService.findByAppIds(Sets.newHashSet(appIds.split(",")));
      }
    }

    @GetMapping("/search")
    public PageDTO<App> searchByAppIdOrAppName(@RequestParam(value = "query", required = false) String query,
        Pageable pageable) {
      if (StringUtils.isEmpty(query)) {
        return appService.findAll(pageable);
      } else {
        return appService.searchByAppIdOrAppName(query, pageable);
      }
    }
}