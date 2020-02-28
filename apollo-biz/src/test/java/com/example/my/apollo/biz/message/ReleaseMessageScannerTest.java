package com.example.my.apollo.biz.message;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import com.example.my.apollo.biz.AbstractUnitTest;
import com.example.my.apollo.biz.config.BizConfig;
import com.example.my.apollo.biz.entity.ReleaseMessage;
import com.example.my.apollo.biz.repository.ReleaseMessageRepository;
import com.google.common.util.concurrent.SettableFuture;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * ReleaseMessageScannerTest
 */
public class ReleaseMessageScannerTest extends AbstractUnitTest {
    private ReleaseMessageScanner releaseMessageScanner;
    @Mock
    private ReleaseMessageRepository releaseMessageRepository;
    @Mock
    private BizConfig bizConfig;
    private int databaseScanInterval;

    @Before
    public void setUp() throws Exception {
        releaseMessageScanner = new ReleaseMessageScanner();
        ReflectionTestUtils.setField(releaseMessageScanner, "releaseMessageRepository", releaseMessageRepository);
        ReflectionTestUtils.setField(releaseMessageScanner, "bizConfig", bizConfig);
        databaseScanInterval = 100; // 100 ms
        when(bizConfig.releaseMessageScanIntervalInMilli()).thenReturn(databaseScanInterval);
        releaseMessageScanner.afterPropertiesSet();
    }

    @Test
    public void testScanMessageAndNotifyMessageListener() throws Exception {
        // 这个写法的意思是 先在listener里面嵌入一个可以拿的出来的变量 [因为在闭包里面变量要先final]
        SettableFuture<ReleaseMessage> someListenerFuture = SettableFuture.create();
        ReleaseMessageListener someListener = (message, channel) -> someListenerFuture.set(message);
        // ReleaseMessageListener someListener = (message, channel) -> outMessage= message;
        releaseMessageScanner.addMessageListener(someListener);

        String someMessage = "someMessage";
        long someId = 100;
        ReleaseMessage someReleaseMessage = assembleReleaseMessage(someId, someMessage);

        when(releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(0L))
                .thenReturn(Lists.newArrayList(someReleaseMessage));

        //把这个变量拿出来,超时时间 5秒
        ReleaseMessage someListenerMessage = someListenerFuture.get(5000,
        TimeUnit.MILLISECONDS);
        
        assertEquals(someMessage, someListenerMessage.getMessage());
        assertEquals(someId, someListenerMessage.getId());

        // SettableFuture<ReleaseMessage> anotherListenerFuture =
        // SettableFuture.create();
        // ReleaseMessageListener anotherListener = (message, channel) ->
        // anotherListenerFuture.set(message);
        // releaseMessageScanner.addMessageListener(anotherListener);

        // String anotherMessage = "anotherMessage";
        // long anotherId = someId + 1;
        // ReleaseMessage anotherReleaseMessage = assembleReleaseMessage(anotherId,
        // anotherMessage);

        // when(releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(someId)).thenReturn(
        // Lists.newArrayList(anotherReleaseMessage));

        // ReleaseMessage anotherListenerMessage =
        // anotherListenerFuture.get(5000, TimeUnit.MILLISECONDS);

        // assertEquals(anotherMessage, anotherListenerMessage.getMessage());
        // assertEquals(anotherId, anotherListenerMessage.getId());

    }

    private ReleaseMessage assembleReleaseMessage(long id, String message) {
        ReleaseMessage releaseMessage = new ReleaseMessage();
        releaseMessage.setId(id);
        releaseMessage.setMessage(message);
        return releaseMessage;
    }
}