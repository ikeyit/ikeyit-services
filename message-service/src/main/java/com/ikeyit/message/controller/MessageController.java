package com.ikeyit.message.controller;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.message.domain.Message;
import com.ikeyit.message.dto.MessageStatsDTO;
import com.ikeyit.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageController {

    @Autowired
    MessageService messageService;


    @PostMapping("/message")
    public void sendMessage(Long fromId, Long toId, Integer messageType, String content) {
        messageService.createMessage(fromId, toId, messageType, content);
    }

    @GetMapping("/message_stats/seller")
    public List<MessageStatsDTO> getMessageStats() {
        return messageService.getUnreadMessageStats(MessageService.MESSAGE_TYPES_SELLER);
    }


    @GetMapping("/messages")
    public Page<Message> getMessages(Integer messageType, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        return messageService.getMessages(null, messageType, new PageParam(page, pageSize));
    }


    @PostMapping("/messages/read")
    public int setMessagesRead(Long[] id) {
        return messageService.setMessagesRead(id);
    }


    @PostMapping("/messages/all_read")
    public int setMessagesAllRead(Integer messageType) {
        return messageService.setMessagesAllRead(messageType);
    }
}


