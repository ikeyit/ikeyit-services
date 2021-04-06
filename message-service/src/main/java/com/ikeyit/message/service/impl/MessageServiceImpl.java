package com.ikeyit.message.service.impl;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.message.domain.Message;
import com.ikeyit.message.dto.MessageStatsDTO;
import com.ikeyit.message.repository.MessageRepository;
import com.ikeyit.message.service.MessageService;
import com.ikeyit.passport.resource.AuthenticationService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    MessageRepository messageRepository;


    @Autowired
    RocketMQTemplate rocketMQTemplate;



    @Override public Message createMessage(Long fromId, Long toId, Integer messageType, String content) {
        Message message = new Message();
        if (fromId == null)
            fromId = 0L;
        message.setFromId(fromId);
        message.setToId(toId);
        message.setMessageType(messageType);
        message.setContent(content);
        message.setStatus(Message.STATUS_UNREAD);
        messageRepository.create(message);

        rocketMQTemplate.syncSend("message", message);
        return message;
    }

    /**
     *
     * @param status
     * @param messageType
     * @param pageParam
     * @return
     */
    @Override public Page<Message> getMessages(Integer status, Integer messageType, PageParam pageParam) {
        Long userId = authenticationService.getCurrentUserId();
        Page<Message> messages = messageRepository.getReceiverMessages(userId, messageType, status, pageParam);
        return messages;
    }


    @Override public int setMessagesAllRead(Integer messageType) {
        Long userId = authenticationService.getCurrentUserId();
        return messageRepository.updateStatusByType(userId, messageType, Message.STATUS_READ);
    }

    @Override public int setMessagesRead(Long[] ids) {
        if (ids.length > 100)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);

        Long userId = authenticationService.getCurrentUserId();
        return messageRepository.updateStatusByIDs(userId, ids, Message.STATUS_READ);
    }


    @Override public Page<Message> getUnreadMessages(Integer messageType, PageParam pageParam) {
        return getMessages(Message.STATUS_UNREAD, messageType, pageParam);
    }

    @Override public List<MessageStatsDTO> getUnreadMessageStats(Integer[] types) {
        Long userId = authenticationService.getCurrentUserId();
        List<MessageStatsDTO> result = new ArrayList();
        for (Integer type : types) {
            long count = messageRepository.countReceiverMessages(userId, type, Message.STATUS_UNREAD);
            result.add(new MessageStatsDTO(type, count));
        }
        return result;
    }

}
