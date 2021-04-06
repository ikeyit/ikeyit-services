package com.ikeyit.message.service;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.message.domain.Message;
import com.ikeyit.message.dto.MessageStatsDTO;

import java.util.List;

public interface MessageService {

    Integer[] MESSAGE_TYPES_SELLER = new Integer[] {
            Message.TYPE_SELLER_TRADE,
            Message.TYPE_SELLER_SYSTEM,
            Message.TYPE_SELLER_SHOP
    };

    
    Message createMessage(Long fromId, Long toId, Integer messageType, String content);

    Page<Message> getMessages(Integer status, Integer messageType, PageParam pageParam);

    int setMessagesAllRead(Integer messageType);

    int setMessagesRead(Long[] ids);

    Page<Message> getUnreadMessages(Integer messageType, PageParam pageParam);

    List<MessageStatsDTO> getUnreadMessageStats(Integer[] types);
}
