package com.example.my.apollo.biz.message;

import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.example.my.apollo.biz.AbstractIntegrationTest;
import com.example.my.apollo.biz.AbstractUnitTest;
import com.example.my.apollo.biz.entity.ReleaseMessage;
import com.example.my.apollo.biz.repository.ReleaseMessageRepository;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @RunWith：用于指定junit运行环境，是junit提供给其他框架测试环境接口扩展，为了便于使用Mockito的依赖注入，
 */
public class DatabaseMessageSenderTest extends AbstractUnitTest{//extends AbstractIntegrationTest  {
    private DatabaseMessageSender messageSender;
    @Autowired
    private ReleaseMessageRepository releaseMessageRepository;

    @Before
    public void setUp() {
        messageSender = new DatabaseMessageSender(releaseMessageRepository);
    }

    @Test
    public void testSendMessage() {
        String someMessage = "some-message";
        long someId = 1;
        ReleaseMessage someReleaseMessage = mock(ReleaseMessage.class);
        when(someReleaseMessage.getId()).thenReturn(someId);
        when(releaseMessageRepository.save(any(ReleaseMessage.class))).thenReturn(someReleaseMessage);

        ArgumentCaptor<ReleaseMessage> captor = ArgumentCaptor.forClass(ReleaseMessage.class);

        messageSender.sendMessage(someMessage, Topics.APOLLO_RELEASE_TOPIC);

        verify(releaseMessageRepository, times(1)).save(captor.capture());//即 releaseMessage.save(待验证的releaseMessage)
        Assert.assertEquals(someMessage, captor.getValue().getMessage());//待验证的releaseMessage 它的getMessage()方法是不是返回["some-message"]
    }

    @Test
    public void testSendUnsupportedMessage() throws Exception {
      String someMessage = "some-message";
      String someUnsupportedTopic = "some-invalid-topic";
      long someId = 1;
      ReleaseMessage someReleaseMessage = mock(ReleaseMessage.class);
      when(someReleaseMessage.getId()).thenReturn(someId);
      when(releaseMessageRepository.save(any(ReleaseMessage.class))).thenReturn(someReleaseMessage); 

      messageSender.sendMessage(someMessage, someUnsupportedTopic);
  
      verify(releaseMessageRepository, never()).save(any(ReleaseMessage.class));//releaseMessageRepository.save(null) 此行代码在sendMessage方法中根本就没执行过
    }
  
    @Test(expected = RuntimeException.class)
    public void testSendMessageFailed() throws Exception {
      String someMessage = "some-message";
      when(releaseMessageRepository.save(any(ReleaseMessage.class))).thenThrow(new RuntimeException());
  
      messageSender.sendMessage(someMessage, Topics.APOLLO_RELEASE_TOPIC);
    }

    /**
     * 清理 ReleaseMessage 任务
     * 执行前 注释掉 AbstractUnitTest 改继承AbstractIntegrationTest
     * @throws InterruptedException
     */
    @Test
    public void testInitialize() throws InterruptedException {
      messageSender.offerIdToCleanQueue(4L);
      //Call private method
      ReflectionTestUtils.invokeMethod(messageSender, "initialize");

      TimeUnit.SECONDS.sleep(50);
    }
}