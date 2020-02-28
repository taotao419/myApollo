package com.example.my.apollo.biz.message;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import com.example.my.apollo.biz.entity.ReleaseMessage;
import com.example.my.apollo.biz.repository.ReleaseMessageRepository;
import com.example.my.apollo.core.utils.ApolloThreadFactory;
import com.example.my.apollo.tracer.Tracer;
import com.example.my.apollo.tracer.spi.Transaction;
import com.google.common.collect.Queues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * DatabaseMessageSender
 */
public class DatabaseMessageSender implements MessageSender {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMessageSender.class);
    // 清理Message队列 最大容量
    private static final int CLEAN_QUEUE_MAX_SIZE = 100;
    // 清理Message队列
    private BlockingQueue<Long> toClean = Queues.newLinkedBlockingQueue(CLEAN_QUEUE_MAX_SIZE);
    private final ExecutorService cleanExecutorService;
    // 是否停止清理Message标识
    private final AtomicBoolean cleanStopped;

    private final ReleaseMessageRepository releaseMessageRepository;

    public DatabaseMessageSender(final ReleaseMessageRepository releaseMessageRepository) {
        cleanExecutorService = Executors
                .newSingleThreadExecutor(ApolloThreadFactory.create("DatabaseMessageSender", true));
        cleanStopped = new AtomicBoolean(false);
        this.releaseMessageRepository = releaseMessageRepository;
    }

    @Override
    @Transactional
    public void sendMessage(String message, String channel) {
        logger.info("Sending message {} to channel {}", message, channel);
        // 仅允许发送APOLLO_RELEASE_TOPIC
        if (!Objects.equals(channel, Topics.APOLLO_RELEASE_TOPIC)) {
            logger.warn("Channel {} not supported by DatabaseMessageSender!");
            return;
        }

        Tracer.logEvent("Apollo.AdminService.ReleaseMessage", message);
        // 开始事务
        Transaction transaction = Tracer.newTransaction("Apollo.AdminService", "sendMessage");
        try {
            // 1. 保存ReleaseMessage对象 数据库
            ReleaseMessage newMessage = releaseMessageRepository.save(new ReleaseMessage(message));
            // 2. 放入清理BlockQueue
            // 队列最多放100个元素,超过直接返回false 不阻塞
            toClean.offer(newMessage.getId());
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable ex) {
            logger.error("Sending message to database failed", ex);
            transaction.setStatus(ex);
            throw ex;
        } finally {
            transaction.complete();
        }

    }

    /**
     * 通知Spring调用,初始化清理ReleaseMessage任务.
     */
    @PostConstruct
    private void initialize() {
        cleanExecutorService.submit(() -> {
            // 若为停止,否则持续运行
            while (!cleanStopped.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    // 1. 队列里取值
                    Long rm = toClean.poll(1, TimeUnit.SECONDS);
                    // 2. 队列非空 开始执行清理任务
                    if (rm != null) {
                        cleanMessage(rm);
                    } else {
                        // 3. 队列空 休息 避免浪费CPU
                        TimeUnit.SECONDS.sleep(5);
                    }
                } catch (Throwable ex) {
                    Tracer.logError(ex);
                }
            }
        });
    }

    private void cleanMessage(Long id) {
        boolean hasMore = true;
        // 1. 查询对应的 ReleaseMessage 对象, 避免已经删除. 因为会在多个进程中执行 N*config service N*Admin
        // Service
        // double check in case the release message is rolled back
        ReleaseMessage releaseMessage = releaseMessageRepository.findById(id).orElse(null);
        if (releaseMessage == null) {
            return;
        }
        // 2. 删除相同消息内容[Message]的老消息
        while (hasMore && !Thread.currentThread().isInterrupted()) {
            // select top 100 * from releaseMessage where message = ? and id < ? order by id
            List<ReleaseMessage> messages = releaseMessageRepository.findFirst100ByMessageAndIdLessThanOrderByIdAsc(
                    releaseMessage.getMessage(), releaseMessage.getId());

            releaseMessageRepository.deleteAll(messages);
            // 如拉取不足100条, 说明没有老消息了
            hasMore = messages.size() == 100;
            // 日志
            messages.forEach(toRemove -> Tracer.logEvent(
                    String.format("ReleaseMessage.Clean.%s", toRemove.getMessage()), String.valueOf(toRemove.getId())));
        }
    }

    void stopClean() {
        cleanStopped.set(true);
    }

    /*********Only for UT *************/
    public void offerIdToCleanQueue(Long id){
        toClean.offer(id);
    }
}