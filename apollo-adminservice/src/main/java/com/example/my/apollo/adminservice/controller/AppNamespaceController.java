package com.example.my.apollo.adminservice.controller;

import com.example.my.apollo.biz.service.AppNamespaceService;
import com.example.my.apollo.biz.service.NamespaceService;
import com.example.my.apollo.common.dto.AppNamespaceDTO;
import com.example.my.apollo.common.entity.AppNamespace;
import com.example.my.apollo.common.exception.BadRequestException;
import com.example.my.apollo.common.utils.BeanUtils;
import com.example.my.apollo.core.enums.ConfigFileFormat;
import com.example.my.apollo.core.utils.StringUtils;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AppNamespaceController
 */
@RestController
public class AppNamespaceController {
    private final AppNamespaceService appNamespaceService;
    private final NamespaceService namespaceService;
    
    public AppNamespaceController(
      final AppNamespaceService appNamespaceService,
      final NamespaceService namespaceService) {
    this.appNamespaceService = appNamespaceService;
    this.namespaceService = namespaceService;
  }

  @PostMapping("/apps/{appId}/appnamespaces")
  public AppNamespaceDTO create(@RequestBody AppNamespaceDTO appNamespace,
                                @RequestParam(defaultValue = "false") boolean silentCreation) {

    //把API层的Object格式[DTO] 再变回common entity
    AppNamespace entity = BeanUtils.transform(AppNamespace.class, appNamespace);
    //可能是已经保存进入数据库了?
    AppNamespace managedEntity = appNamespaceService.findOne(entity.getAppId(), entity.getName());

    if (managedEntity == null) {
      //全新的appNamespace
      if (StringUtils.isEmpty(entity.getFormat())){
        entity.setFormat(ConfigFileFormat.Properties.getValue());//赋值默认的Format为[PROPERTIES]
      }
      //调用service层 保存entity
      entity = appNamespaceService.createAppNamespace(entity);
    } else if (silentCreation) {//??
      appNamespaceService.createNamespaceForAppNamespaceInAllCluster(appNamespace.getAppId(), appNamespace.getName(),
          appNamespace.getDataChangeCreatedBy());

      entity = managedEntity;
    } else {
      throw new BadRequestException("app namespaces already exist.");
    }

    return BeanUtils.transform(AppNamespaceDTO.class, entity);
  }
}