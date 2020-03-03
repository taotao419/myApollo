package com.example.my.apollo.configservice.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.example.my.apollo.biz.config.BizConfig;
import com.example.my.apollo.biz.entity.ReleaseMessage;
import com.example.my.apollo.biz.message.ReleaseMessageListener;
import com.example.my.apollo.biz.message.Topics;
import com.example.my.apollo.biz.repository.ReleaseMessageRepository;
import com.example.my.apollo.core.utils.ApolloThreadFactory;
import com.example.my.apollo.tracer.Tracer;
import com.example.my.apollo.tracer.spi.Transaction;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 缓存 ReleaseMessage 的 Service 实现类。通过将 ReleaseMessage 缓存在内存中，提高查询性能。 缓存实现方式如下：
 * 启动时，初始化 ReleaseMessage 到缓存。 新增时，基于 ReleaseMessageListener ，通知有新的
 * ReleaseMessage ，根据是否有消息间隙，直接使用该 ReleaseMessage 或从数据库读取。
 */
@Service
public class ReleaseMessageServiceWithCache implements ReleaseMessageListener, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(ReleaseMessageServiceWithCache.class);
    private final ReleaseMessageRepository releaseMessageRepository;
    private final BizConfig bizConfig;

    private int scanInterval;
    private TimeUnit scanIntervalTimeUnit;

    private volatile long maxIdScanned;

    private ConcurrentMap<String, ReleaseMessage> releaseMessageCache;

    private AtomicBoolean doScan;
    private ExecutorService executorService;

    public ReleaseMessageServiceWithCache(final ReleaseMessageRepository releaseMessageRepository,
            final BizConfig bizConfig) {
        this.releaseMessageRepository = releaseMessageRepository;
        this.bizConfig = bizConfig;
        initialize();
    }

    private void initialize() {
        releaseMessageCache = Maps.newConcurrentMap();
        doScan = new AtomicBoolean(true);
        executorService = Executors
                .newSingleThreadExecutor(ApolloThreadFactory.create("ReleaseMessageServiceWithCache", true));
    }

    public ReleaseMessage findLatestReleaseMessageForMessages(Set<String> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            return null;
        }

        long maxReleaseMessageId = 0;
        ReleaseMessage result = null;
        for (String message : messages) {
            ReleaseMessage releaseMessage = releaseMessageCache.get(message);
            if (releaseMessage != null && releaseMessage.getId() > maxReleaseMessageId) {
                maxReleaseMessageId = releaseMessage.getId();
                result = releaseMessage;
            }
        }

        return result;
    }

    public List<ReleaseMessage> findLatestReleaseMessagesGroupByMessages(Set<String> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            return Collections.emptyList();
        }
        List<ReleaseMessage> releaseMessages = Lists.newArrayList();

        for (String message : messages) {
            ReleaseMessage releaseMessage = releaseMessageCache.get(message);
            if (releaseMessage != null) {
                releaseMessages.add(releaseMessage);
            }
        }

        return releaseMessages;
    }

    /**
     * 产生的 ReleaseMessage 遗漏的问题。为什么会遗漏呢？笔者又去请教作者，🙂 给 666 个赞。
        1) 20:00:00 程序启动过程中，当前 release message 有 5 条
        2) 20:00:01 loadReleaseMessages(0); 执行完成，获取到 5 条记录
        3) 20:00:02 有一条 release message 新产生，但是因为程序还没启动完，所以不会触发 handle message 操作
        4) 20:00:05 程序启动完成，但是第三步的这条新的 release message 漏了
        5) 20:10:00 假设这时又有一条 release message 产生，这次会触发 handle message ，同时会把第三步的那条 release message 加载到
        所以，定期刷的机制就是为了解决第三步中产生的release message问题。
        当程序启动完，handleMessage生效后，就不需要再定期扫了
     */
    @Override
    public void handleMessage(ReleaseMessage message, String channel) {
        // Could stop once the ReleaseMessageScanner starts to work
        doScan.set(false);
        logger.info("message received - channel: {}, message: {}", channel, message);

        String content = message.getMessage();
        Tracer.logEvent("Apollo.ReleaseMessageService.UpdateCache", String.valueOf(message.getId()));
        if (!Topics.APOLLO_RELEASE_TOPIC.equals(channel) || Strings.isNullOrEmpty(content)) {
            return;
        }

        long gap = message.getId() - maxIdScanned;
        if (gap == 1) {//message如果是挨着最近读过的MessageId,直接替换缓存
            mergeReleaseMessage(message);
        } else if (gap > 1) {
            // gap found! 走到这里说明有一些message被跳过了,需要读数据库. ex:内存里最大messageId是5,入参里Message.getId()是10,说明第[6-9]条message漏了
            loadReleaseMessages(maxIdScanned);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        populateDataBaseInterval();
        // block the startup process until load finished
        // this should happen before ReleaseMessageScanner due to autowire
        loadReleaseMessages(0);//从0开始从数据库读取message [还好有一个线程默默的在数据库里删除数据,不然message太多了]

        executorService.submit(() -> {
            while (doScan.get() && !Thread.currentThread().isInterrupted()) {
                Transaction transaction = Tracer.newTransaction("Apollo.ReleaseMessageServiceWithCache",
                        "scanNewReleaseMessages");
                try {
                    loadReleaseMessages(maxIdScanned);
                    transaction.setStatus(Transaction.SUCCESS);
                } catch (Throwable ex) {
                    transaction.setStatus(ex);
                    logger.error("Scan new release messages failed", ex);
                } finally {
                    transaction.complete();
                }
                try {
                    scanIntervalTimeUnit.sleep(scanInterval);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        });
    }

    /**
     * 缓存里替换掉老的releaseMessge 也就是更新啦
     */
    private synchronized void mergeReleaseMessage(ReleaseMessage releaseMessage) {
        ReleaseMessage old = releaseMessageCache.get(releaseMessage.getMessage());
        if (old == null || releaseMessage.getId() > old.getId()) {
            releaseMessageCache.put(releaseMessage.getMessage(), releaseMessage);
            maxIdScanned = releaseMessage.getId();
        }
    }

    /**
     * 从数据库里拿releaseMessage,并更新缓存
     * @param startId 只拿大于startId的message
     */
    private void loadReleaseMessages(long startId) {
        boolean hasMore = true;
        while (hasMore && !Thread.currentThread().isInterrupted()) {
            // current batch is 500
            List<ReleaseMessage> releaseMessages = releaseMessageRepository
                    .findFirst500ByIdGreaterThanOrderByIdAsc(startId);
            if (CollectionUtils.isEmpty(releaseMessages)) {
                break;
            }
            // 所有的messages都试着更新缓存
            releaseMessages.forEach(this::mergeReleaseMessage);// 即C#中 forEach(()->{ someMethod() })
            int scanned = releaseMessages.size();
            startId = releaseMessages.get(scanned - 1).getId();
            hasMore = scanned == 500;
            logger.info("Loaded {} release messages with startId {}", scanned, startId);
        }
    }

    private void populateDataBaseInterval() {
        scanInterval = bizConfig.releaseMessageCacheScanInterval();
        scanIntervalTimeUnit = bizConfig.releaseMessageCacheScanIntervalTimeUnit();
    }

    // only for test use
    public void reset() throws Exception {
        executorService.shutdownNow();
        initialize();
        afterPropertiesSet();
    }
}