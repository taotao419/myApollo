package com.example.my.apollo.biz.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.example.my.apollo.biz.entity.ReleaseMessage;
import com.example.my.apollo.biz.repository.ReleaseMessageRepository;
import com.example.my.apollo.tracer.Tracer;
import com.google.common.collect.Lists;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * ReleaseMessageService
 */
@Service
public class ReleaseMessageService {
  private final ReleaseMessageRepository releaseMessageRepository;

  public ReleaseMessageService(final ReleaseMessageRepository releaseMessageRepository) {
    this.releaseMessageRepository = releaseMessageRepository;
  }

  public ReleaseMessage findLatestReleaseMessageForMessages(Collection<String> messages) {
    if (CollectionUtils.isEmpty(messages)) {
      return null;
    }
    return releaseMessageRepository.findTopByMessageInOrderByIdDesc(messages);
  }

  /**
   * 格式转换 List<Object[]> --> List<ReleaseMessage>
   */
  public List<ReleaseMessage> findLatestReleaseMessagesGroupByMessages(Collection<String> messages) {
    if (CollectionUtils.isEmpty(messages)) {
      return Collections.emptyList();
    }
    List<Object[]> result = releaseMessageRepository.findLatestReleaseMessagesGroupByMessages(messages);
    List<ReleaseMessage> releaseMessages = Lists.newArrayList();
    for (Object[] o : result) {
      try {
        ReleaseMessage releaseMessage = new ReleaseMessage((String) o[0]);
        releaseMessage.setId((Long) o[1]);
        releaseMessages.add(releaseMessage);
      } catch (Exception ex) {
        Tracer.logError("Parsing LatestReleaseMessagesGroupByMessages failed", ex);
      }
    }
    return releaseMessages;
  }
}