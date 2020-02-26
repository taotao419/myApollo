package com.example.my.apollo.biz.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import com.example.my.apollo.biz.config.BizConfig;
import com.example.my.apollo.biz.entity.Audit;
import com.example.my.apollo.biz.entity.Item;
import com.example.my.apollo.biz.entity.Namespace;
import com.example.my.apollo.biz.repository.ItemRepository;
import com.example.my.apollo.common.exception.BadRequestException;
import com.example.my.apollo.common.exception.NotFoundException;
import com.example.my.apollo.common.utils.BeanUtils;
import com.example.my.apollo.core.utils.StringUtils;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final NamespaceService namespaceService;
    private final AuditService auditService;
    private final BizConfig bizConfig;

    /**
     * ItemService构造时引用了namespaceService, 而且namespaceService在构造时也引用了ItemService,
     * 这样不就导致了循环引用了嘛. 可以用@Lazy避免循环引用 lazy的原理如下: 1. A的创建: A a=new A(); 2.
     * 属性注入:发现需要B,查询字段b的所有注解,发现有@lazy注解,那么就不直接创建B了,而是使用动态代理创建一个代理类B 3.
     * 此时A跟B就不是相互依赖了,变成了A依赖一个代理类B1,B依赖A
     */
    public ItemService(final ItemRepository itemRepository, final @Lazy NamespaceService namespaceService,
            final AuditService auditService, final BizConfig bizConfig) {
        this.itemRepository = itemRepository;
        this.namespaceService = namespaceService;
        this.auditService = auditService;
        this.bizConfig=bizConfig;
    }

    @Transactional
    public Item delete(long id, String operator) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            throw new IllegalArgumentException("item not exist. ID:" + id);
        }

        item.setDeleted(true);
        item.setDataChangeLastModifiedBy(operator);
        Item deletedItem = itemRepository.save(item);

        auditService.audit(Item.class.getSimpleName(), id, Audit.OP.DELETE, operator);
        return deletedItem;
    }

    @Transactional
    public int batchDelete(long namespaceId, String operator) {
        return itemRepository.deleteByNamespaceId(namespaceId, operator);

    }

    public Item findOne(String appId, String clusterName, String namespaceName, String key) {
        Namespace namespace = namespaceService.findOne(appId, clusterName, namespaceName);
        if (namespace == null) {
            throw new NotFoundException(
                    String.format("namespace not found for %s %s %s", appId, clusterName, namespaceName));
        }
        Item item = itemRepository.findByNamespaceIdAndKey(namespace.getId(), key);
        return item;
    }

    public Item findLastOne(String appId, String clusterName, String namespaceName) {
        Namespace namespace = namespaceService.findOne(appId, clusterName, namespaceName);
        if (namespace == null) {
          throw new NotFoundException(
              String.format("namespace not found for %s %s %s", appId, clusterName, namespaceName));
        }
        return findLastOne(namespace.getId());
      }
    
      public Item findLastOne(long namespaceId) {
        return itemRepository.findFirst1ByNamespaceIdOrderByLineNumDesc(namespaceId);
      }
    
      public Item findOne(long itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        return item;
      }
      
      public List<Item> findItemsWithoutOrdered(Long namespaceId) {
        List<Item> items = itemRepository.findByNamespaceId(namespaceId);
        if (items == null) {
          return Collections.emptyList();
        }
        return items;
      }
    
      public List<Item> findItemsWithoutOrdered(String appId, String clusterName, String namespaceName) {
        Namespace namespace = namespaceService.findOne(appId, clusterName, namespaceName);
        if (namespace != null) {
          return findItemsWithoutOrdered(namespace.getId());
        } else {
          return Collections.emptyList();
        }
      }
    
      public List<Item> findItemsWithOrdered(Long namespaceId) {
        List<Item> items = itemRepository.findByNamespaceIdOrderByLineNumAsc(namespaceId);
        if (items == null) {
          return Collections.emptyList();
        }
        return items;
      }
    
      public List<Item> findItemsWithOrdered(String appId, String clusterName, String namespaceName) {
        Namespace namespace = namespaceService.findOne(appId, clusterName, namespaceName);
        if (namespace != null) {
          return findItemsWithOrdered(namespace.getId());
        } else {
          return Collections.emptyList();
        }
      }
    
      public List<Item> findItemsModifiedAfterDate(long namespaceId, Date date) {
        return itemRepository.findByNamespaceIdAndDataChangeLastModifiedTimeGreaterThan(namespaceId, date);
      }

      @Transactional
      public Item save(Item entity) {
        //1. 校检key/value 长度
        checkItemKeyLength(entity.getKey());
        checkItemValueLength(entity.getNamespaceId(), entity.getValue());
    
        entity.setId(0);//protection
        //2. 赋值lineNum
        if (entity.getLineNum() == 0) {
          Item lastItem = findLastOne(entity.getNamespaceId());
          int lineNum = lastItem == null ? 1 : lastItem.getLineNum() + 1;
          entity.setLineNum(lineNum);
        }
        //3. repo层保存
        Item item = itemRepository.save(entity);
        //4. audit 审核
        auditService.audit(Item.class.getSimpleName(), item.getId(), Audit.OP.INSERT,
                           item.getDataChangeCreatedBy());
    
        return item;
      }
    
      @Transactional
      public Item update(Item item) {
        checkItemValueLength(item.getNamespaceId(), item.getValue());
        Item managedItem = itemRepository.findById(item.getId()).orElse(null);
        BeanUtils.copyEntityProperties(item, managedItem);
        managedItem = itemRepository.save(managedItem);
    
        auditService.audit(Item.class.getSimpleName(), managedItem.getId(), Audit.OP.UPDATE,
                           managedItem.getDataChangeLastModifiedBy());
    
        return managedItem;
      }
    
      private boolean checkItemValueLength(long namespaceId, String value) {
        int limit = getItemValueLengthLimit(namespaceId);
        if (!StringUtils.isEmpty(value) && value.length() > limit) {
          throw new BadRequestException("value too long. length limit:" + limit);
        }
        return true;
      }
    
      private boolean checkItemKeyLength(String key) {
        if (!StringUtils.isEmpty(key) && key.length() > bizConfig.itemKeyLengthLimit()) {
          throw new BadRequestException("key too long. length limit:" + bizConfig.itemKeyLengthLimit());
        }
        return true;
      }
    
      private int getItemValueLengthLimit(long namespaceId) {
        Map<Long, Integer> namespaceValueLengthOverride = bizConfig.namespaceValueLengthLimitOverride();
        if (namespaceValueLengthOverride != null && namespaceValueLengthOverride.containsKey(namespaceId)) {
          return namespaceValueLengthOverride.get(namespaceId);
        }
        return bizConfig.itemValueLengthLimit();
      }
}