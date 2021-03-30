package com.ikeyit.message.mq;


import com.ikeyit.message.domain.Message;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 消息队列获得消息
 */
@RocketMQMessageListener(topic = "message", consumerGroup = "message", messageModel = MessageModel.BROADCASTING)
@Component
public class MessageListener implements RocketMQListener<Message> {

    private static Logger log = LoggerFactory.getLogger(MessageListener.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;



    @Override
    public void onMessage(Message message) {
        log.debug("[MQ]用户消息! {}", message);
        messagingTemplate.convertAndSendToUser(message.getToId().toString(), "/queue/message", message);
    }

}
