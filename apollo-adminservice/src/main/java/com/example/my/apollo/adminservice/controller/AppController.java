package com.example.my.apollo.adminservice.controller;

import java.util.List;

import javax.validation.Valid;

import com.example.my.apollo.biz.service.AdminService;
import com.example.my.apollo.biz.service.AppService;
import com.example.my.apollo.common.dto.AppDTO;
import com.example.my.apollo.common.entity.App;
import com.example.my.apollo.common.exception.BadRequestException;
import com.example.my.apollo.common.exception.NotFoundException;
import com.example.my.apollo.common.utils.BeanUtils;
import com.example.my.apollo.core.utils.StringUtils;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {
    private final AppService appService;
    private final AdminService adminService;

    public AppController(final AppService appService, final AdminService adminService) {
        this.appService = appService;
        this.adminService = adminService;
    }

    @PostMapping("/apps")
    public AppDTO create(@Valid @RequestBody AppDTO dto) {
        App entity = BeanUtils.transform(App.class, dto);
        // 防御代码 已经有Appid就抛错
        App managedEntity = appService.findOne(entity.getAppId());
        if (managedEntity != null) {
            throw new BadRequestException("app already exist.");
        }

        entity = adminService.createNewApp(entity);

        return BeanUtils.transform(AppDTO.class, entity);
    }

    @GetMapping("/apps")
    public List<AppDTO> find(@RequestParam(value = "name", required = false) String name, Pageable pageable) {
        List<App> app = null;
        if (StringUtils.isBlank(name)) {
            app = appService.findAll(pageable);
        } else {
            app = appService.findByName(name);
        }
        return BeanUtils.batchTransform(AppDTO.class, app);
    }

    @GetMapping("/apps/{appId:.+}")
    public AppDTO get(@PathVariable("appId") String appId) {
        App app = appService.findOne(appId);
        if (app == null) {
            throw new NotFoundException("app not found for appId " + appId);
        }
        return BeanUtils.transform(AppDTO.class, app);
    }
}