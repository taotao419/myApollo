package com.example.my.apollo.biz.message;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.my.apollo.biz.config.BizConfig;
import com.example.my.apollo.biz.entity.ReleaseMessage;
import com.example.my.apollo.biz.repository.ReleaseMessageRepository;
import com.example.my.apollo.core.utils.ApolloThreadFactory;
import com.example.my.apollo.tracer.Tracer;
import com.example.my.apollo.tracer.spi.Transaction;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * ReleaseMessageScanner
 */
public class ReleaseMessageScanner implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ReleaseMessageScanner.class);
    @Autowired
    private BizConfig bizConfig;
    @Autowired
    private ReleaseMessageRepository releaseMessageRepository;
    // 从数据库扫描ReleaseMessage表的频率 单位:毫秒
    private int databaseScanInterval;
    // 监听器数组
    private List<ReleaseMessageListener> listeners;
    // 定时任务
    private ScheduledExecutorService executorService;
    private long maxIdScanned;

    public ReleaseMessageScanner() {
        listeners = Lists.newCopyOnWriteArrayList();
        executorService = Executors.newScheduledThreadPool(1,
                ApolloThreadFactory.create("ReleaseMessageScanner", true));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 1. 从 ServerConfig 中获得频率,默认1000ms
        databaseScanInterval = bizConfig.releaseMessageScanIntervalInMilli();
        // 2. 获得最大ReleaseMessage id
        maxIdScanned = loadLargestMessageId();
        executorService.scheduleWithFixedDelay((Runnable) () -> {
            Transaction transaction = Tracer.newTransaction("Apollo.ReleaseMessageScanner", "scanMessage");
            try {
                // 3. 从DB中 扫描ReleaseMessage
                scanMessages();
                transaction.setStatus(Transaction.SUCCESS);
            } catch (Throwable ex) {
                transaction.setStatus(ex);
                logger.error("Scan and send message failed", ex);
            } finally {
                transaction.complete();
            }
        }, databaseScanInterval, databaseScanInterval, TimeUnit.MILLISECONDS);

    }

    /**
     * add message listeners for release message
     * 
     * @param listener
     */
    public void addMessageListener(ReleaseMessageListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Scan messages, continue scanning until there is no more messages
     */
    private void scanMessages() {
        boolean hasMoreMessages = true;
        while (hasMoreMessages && !Thread.currentThread().isInterrupted()) {
            hasMoreMessages = scanAndSendMessages();
        }
    }

    /**
     * scan messages and send
     *
     * @return whether there are more messages
     */
    private boolean scanAndSendMessages() {
        // current batch is 500
        List<ReleaseMessage> releaseMessages = releaseMessageRepository
                .findFirst500ByIdGreaterThanOrderByIdAsc(maxIdScanned);
        if (CollectionUtils.isEmpty(releaseMessages)) {
            return false;
        }
        // 触发监听器
        fireMessageScanned(releaseMessages);
        int messageScanned = releaseMessages.size();
        maxIdScanned = releaseMessages.get(messageScanned - 1).getId();
        return messageScanned == 500;// 拉了500条 说明还有新消息,如果没有满500条,说明没有新消息了
    }

    /**
     * find largest message id as the current start point
     * 
     * @return current largest message id
     */
    private long loadLargestMessageId() {
        ReleaseMessage releaseMessage = releaseMessageRepository.findTopByOrderByIdDesc();
        return releaseMessage == null ? 0 : releaseMessage.getId();
    }

    /**
     * Notify listeners with messages loaded
     * 
     * @param messages
     */
    private void fireMessageScanned(List<ReleaseMessage> messages) {
        for (ReleaseMessage message : messages) {
            for (ReleaseMessageListener listener : listeners) {
                try {
                    listener.handleMessage(message, Topics.APOLLO_RELEASE_TOPIC);
                } catch (Throwable ex) {
                    Tracer.logError(ex);
                    logger.error("Failed to invoke message listener {}", listener.getClass(), ex);
                }
            }
        }
    }
}