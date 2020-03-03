package com.example.my.apollo.configservice.controller;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.example.my.apollo.biz.config.BizConfig;
import com.example.my.apollo.biz.entity.ReleaseMessage;
import com.example.my.apollo.biz.message.ReleaseMessageListener;
import com.example.my.apollo.biz.message.Topics;
import com.example.my.apollo.biz.utils.EntityManagerUtil;
import com.example.my.apollo.common.exception.BadRequestException;
import com.example.my.apollo.configservice.service.ReleaseMessageServiceWithCache;
import com.example.my.apollo.configservice.util.NamespaceUtil;
import com.example.my.apollo.configservice.util.WatchKeysUtil;
import com.example.my.apollo.configservice.wrapper.DeferredResultWrapper;
import com.example.my.apollo.core.ConfigConsts;
import com.example.my.apollo.core.dto.ApolloConfigNotification;
import com.example.my.apollo.core.utils.ApolloThreadFactory;
import com.example.my.apollo.tracer.Tracer;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/notifications/v2")
public class NotificationControllerV2 implements ReleaseMessageListener {

  private static final Logger logger = LoggerFactory.getLogger(NotificationControllerV2.class);
  /**
   * 当请求的 Namespace 暂无新通知时，会将该 Namespace 对应的 Watch Key 们，注册到 deferredResults 中。
   * 等到 Namespace 配置发生变更时，在 #handleMessage(...) 中，进行通知。
   * 
   * 非常关键的字段.
   */
  private final Multimap<String, DeferredResultWrapper> deferredResults = Multimaps
      .synchronizedSetMultimap(HashMultimap.create());
  private static final Splitter STRING_SPLITTER = Splitter.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR)
      .omitEmptyStrings();
  private static final Type notificationsTypeReference = new TypeToken<List<ApolloConfigNotification>>() {
  }.getType();

  private final ExecutorService largeNotificationBatchExecutorService;

  private final WatchKeysUtil watchKeysUtil;
  private final ReleaseMessageServiceWithCache releaseMessageService;
  private final EntityManagerUtil entityManagerUtil;
  private final NamespaceUtil namespaceUtil;
  private final Gson gson;
  private final BizConfig bizConfig;

  public NotificationControllerV2(final WatchKeysUtil watchKeysUtil,
      final ReleaseMessageServiceWithCache releaseMessageService, final EntityManagerUtil entityManagerUtil,
      final NamespaceUtil namespaceUtil, final Gson gson, final BizConfig bizConfig) {
    largeNotificationBatchExecutorService = Executors
        .newSingleThreadExecutor(ApolloThreadFactory.create("NotificationControllerV2", true));
    this.watchKeysUtil = watchKeysUtil;
    this.releaseMessageService = releaseMessageService;
    this.entityManagerUtil = entityManagerUtil;
    this.namespaceUtil = namespaceUtil;
    this.gson = gson;
    this.bizConfig = bizConfig;
  }

  @GetMapping
  public DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> pollNotification(
      @RequestParam(value = "appId") String appId, @RequestParam(value = "cluster") String cluster,
      @RequestParam(value = "notifications") String notificationsAsString,
      @RequestParam(value = "dataCenter", required = false) String dataCenter,
      @RequestParam(value = "ip", required = false) String clientIp) {
    List<ApolloConfigNotification> notifications = null;// 从客户端请求的Namespace (长轮询)

    try {
      notifications = gson.fromJson(notificationsAsString, notificationsTypeReference);
    } catch (Throwable ex) {
      Tracer.logError(ex);
    }
    // 防御代码 防止notifications为空
    if (CollectionUtils.isEmpty(notifications)) {
      throw new BadRequestException("Invalid format of notifications: " + notificationsAsString);
    }

    DeferredResultWrapper deferredResultWrapper = new DeferredResultWrapper(bizConfig.longPollingTimeoutInMilli());
    Set<String> namespaces = Sets.newHashSet();
    Map<String, Long> clientSideNotifications = Maps.newHashMap();
    Map<String, ApolloConfigNotification> filteredNotifications = filterNotifications(appId, notifications);// 过滤下
                                                                                                            // 主要是看看数据库有没有此Namespace

    for (Map.Entry<String, ApolloConfigNotification> notificationEntry : filteredNotifications.entrySet()) {
      String normalizedNamespace = notificationEntry.getKey();
      ApolloConfigNotification notification = notificationEntry.getValue();
      namespaces.add(normalizedNamespace);
      clientSideNotifications.put(normalizedNamespace, notification.getNotificationId());// 可以认为NotificationId就是客户端中namespace的版本号
      if (!Objects.equals(notification.getNamespaceName(), normalizedNamespace)) {
        deferredResultWrapper.recordNamespaceNameNormalizedResult(notification.getNamespaceName(), normalizedNamespace);
      }
    }

    if (CollectionUtils.isEmpty(namespaces)) {
      throw new BadRequestException("Invalid format of notifications: " + notificationsAsString);
    }

    Multimap<String, String> watchedKeysMap = watchKeysUtil.assembleAllWatchKeys(appId, cluster, namespaces,
        dataCenter);

    Set<String> watchedKeys = Sets.newHashSet(watchedKeysMap.values());

    /**
     * 1、set deferredResult before the check, for avoid more waiting If the check
     * before setting deferredResult,it may receive a notification the next time
     * when method handleMessage is executed between check and set deferredResult.
     */
    // 超时事件
    deferredResultWrapper.onTimeout(() -> logWatchedKeys(watchedKeys, "Apollo.LongPoll.TimeOutKeys"));
    // 完毕事件 namespace 作为key, 将不监控了.
    deferredResultWrapper.onCompletion(() -> {
      // unregister all keys
      for (String key : watchedKeys) {
        deferredResults.remove(key, deferredResultWrapper);
      }
      logWatchedKeys(watchedKeys, "Apollo.LongPoll.CompletedKeys");
    });

    // register all keys
    // deferredResults [key, List<wrapper>]
    // 监控namespace的通知
    for (String key : watchedKeys) {
      this.deferredResults.put(key, deferredResultWrapper);
    }

    logWatchedKeys(watchedKeys, "Apollo.LongPoll.RegisteredKeys");
    logger.debug("Listening {} from appId: {}, cluster: {}, namespace: {}, datacenter: {}", watchedKeys, appId, cluster,
        namespaces, dataCenter);

    /**
     * 2、check new release
     */
    List<ReleaseMessage> latestReleaseMessages = releaseMessageService
        .findLatestReleaseMessagesGroupByMessages(watchedKeys);// 从缓存里拿的

    /**
     * Manually close the entity manager. Since for async request, Spring won't do
     * so until the request is finished, which is unacceptable since we are doing
     * long polling - means the db connection would be hold for a very long time
     */
    entityManagerUtil.closeEntityManager();
    // 得到了更新过namespace的消息 (比客户端版本号大)
    List<ApolloConfigNotification> newNotifications = getApolloConfigNotifications(namespaces, clientSideNotifications,
        watchedKeysMap, latestReleaseMessages);
   
    /**
     * 是说Servlet 3.0支持了业务请求的异步处理，Servlet3之前一个请求的处理流程， 请求解析、READ BODY,RESPONSE
     * BODY,以及其中的业务逻辑处理都由Tomcat线程池中的一个线程进行处理的。
     * 那么3.0以后我们可以让请求线程(IO线程)和业务处理线程分开，进而对业务进行线程池隔离。
     * 我们还可以根据业务重要性进行业务分级，然后再把线程池分级。还可以根据这些分级做其它操作比如监控和降级处理。 servlet 3.0 从 tomcat
     * 7.x 开始支持。 Spring MVC Async DeferredResult是Spring MVC 3.2 以上版本基于Servlet
     * 3的基础做的封装，原理及实现方式同上
     */
    // 赋值到deferredResultWrapper  意味着 请求的时候立即从ReleaseMessageServiceWithCache取一下,如果有通知 立即返回
    // 如果当下没有新通知, deferredResultWrapper.getResult() 会等待60秒, 如果有通知(外部调用handleMessage)也会立即返回
    // 60秒后 返回304 Not Modified.
    if (!CollectionUtils.isEmpty(newNotifications)) {
      deferredResultWrapper.setResult(newNotifications);
    }

    return deferredResultWrapper.getResult();
  }

  /**
   * 去.properties后缀 & namespace变小写 & 数据库确认有这个Namespace
   */
  private Map<String, ApolloConfigNotification> filterNotifications(String appId,
      List<ApolloConfigNotification> notifications) {
    Map<String, ApolloConfigNotification> filteredNotifications = Maps.newHashMap();
    for (ApolloConfigNotification notification : notifications) {
      if (Strings.isNullOrEmpty(notification.getNamespaceName())) {
        continue;
      }
      // 去掉.properties后缀
      String originalNamespace = namespaceUtil.filterNamespaceName(notification.getNamespaceName());
      notification.setNamespaceName(originalNamespace);
      // fix the character case issue, such as FX.apollo <-> fx.apollo
      // namespace 改成小写 [core class CaseInsensitiveMapWrapper]
      String normalizedNamespace = namespaceUtil.normalizeNamespace(appId, originalNamespace);

      // in case client side namespace name has character case issue and has
      // difference notification ids
      // such as FX.apollo = 1 but fx.apollo = 2, we should let FX.apollo have the
      // chance to update its notification id
      // which means we should record FX.apollo = 1 here and ignore fx.apollo = 2
      if (filteredNotifications.containsKey(normalizedNamespace)
          && filteredNotifications.get(normalizedNamespace).getNotificationId() < notification.getNotificationId()) {
        continue;
      }

      filteredNotifications.put(normalizedNamespace, notification);
    }
    return filteredNotifications;
  }

  /**
   * 得到了更新过namespace的消息 (比客户端版本号大)
   */
  private List<ApolloConfigNotification> getApolloConfigNotifications(Set<String> namespaces,
      Map<String, Long> clientSideNotifications, Multimap<String, String> watchedKeysMap,
      List<ReleaseMessage> latestReleaseMessages) {
    List<ApolloConfigNotification> newNotifications = Lists.newArrayList();
    if (!CollectionUtils.isEmpty(latestReleaseMessages)) {
      // latestReleaseMessages -> latestNotifications [list转换成map]
      Map<String, Long> latestNotifications = Maps.newHashMap();
      for (ReleaseMessage releaseMessage : latestReleaseMessages) {
        latestNotifications.put(releaseMessage.getMessage(), releaseMessage.getId());
      }

      for (String namespace : namespaces) {
        // 比较客户端的版本号和最新数据库里的 [notificationId]
        long clientSideId = clientSideNotifications.get(namespace);
        long latestId = ConfigConsts.NOTIFICATION_ID_PLACEHOLDER;
        Collection<String> namespaceWatchedKeys = watchedKeysMap.get(namespace);
        for (String namespaceWatchedKey : namespaceWatchedKeys) {
          long namespaceNotificationId = latestNotifications.getOrDefault(namespaceWatchedKey,
              ConfigConsts.NOTIFICATION_ID_PLACEHOLDER);
          if (namespaceNotificationId > latestId) {
            latestId = namespaceNotificationId;
          }
        }
        // 如果数据库里版本号大 就放入newNotifications 返回结果
        if (latestId > clientSideId) {
          ApolloConfigNotification notification = new ApolloConfigNotification(namespace, latestId);
          namespaceWatchedKeys.stream().filter(latestNotifications::containsKey)
              .forEach(namespaceWatchedKey -> notification.addMessage(namespaceWatchedKey,
                  latestNotifications.get(namespaceWatchedKey)));
          newNotifications.add(notification);
        }
      }
    }
    return newNotifications;
  }

  @Override
  public void handleMessage(ReleaseMessage message, String channel) {
    logger.info("message received - channel: {}, message: {}", channel, message);

    String content = message.getMessage();
    Tracer.logEvent("Apollo.LongPoll.Messages", content);
    if (!Topics.APOLLO_RELEASE_TOPIC.equals(channel) || Strings.isNullOrEmpty(content)) {
      return;
    }

    String changedNamespace = retrieveNamespaceFromReleaseMessage.apply(content);

    if (Strings.isNullOrEmpty(changedNamespace)) {
      logger.error("message format invalid - {}", content);
      return;
    }

    if (!deferredResults.containsKey(content)) {
      return;
    }

    // create a new list to avoid ConcurrentModificationException
    List<DeferredResultWrapper> results = Lists.newArrayList(deferredResults.get(content));

    ApolloConfigNotification configNotification = new ApolloConfigNotification(changedNamespace, message.getId());
    configNotification.addMessage(content, message.getId());

    // do async notification if too many clients
    // 这个if 分支 [异步]是为了处理一个namespace有更新 需要通知非常多客户端的情况. 分批deferredResult.setResult
    if (results.size() > bizConfig.releaseMessageNotificationBatch()) {
      largeNotificationBatchExecutorService.submit(() -> {
        logger.debug("Async notify {} clients for key {} with batch {}", results.size(), content,
            bizConfig.releaseMessageNotificationBatch());
        for (int i = 0; i < results.size(); i++) {
          if (i > 0 && i % bizConfig.releaseMessageNotificationBatch() == 0) {
            try {
              //sleep 100 毫秒
              TimeUnit.MILLISECONDS.sleep(bizConfig.releaseMessageNotificationBatchIntervalInMilli());
            } catch (InterruptedException e) {
              // ignore
            }
          }
          logger.debug("Async notify {}", results.get(i));
          results.get(i).setResult(configNotification);
        }
      });
      return;
    }

    logger.debug("Notify {} clients for key {}", results.size(), content);
    //普通情况下[同步] 就循环通知客户端即可
    for (DeferredResultWrapper result : results) {
      result.setResult(configNotification);
    }
    logger.debug("Notification completed");
  }

  private static final Function<String, String> retrieveNamespaceFromReleaseMessage = releaseMessage -> {
    if (Strings.isNullOrEmpty(releaseMessage)) {
      return null;
    }
    List<String> keys = STRING_SPLITTER.splitToList(releaseMessage);
    // message should be appId+cluster+namespace
    if (keys.size() != 3) {
      logger.error("message format invalid - {}", releaseMessage);
      return null;
    }
    return keys.get(2);
  };

  private void logWatchedKeys(Set<String> watchedKeys, String eventName) {
    for (String watchedKey : watchedKeys) {
      Tracer.logEvent(eventName, watchedKey);
    }
  }
}