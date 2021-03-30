package com.ikeyit.mqhelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
/**
 * MQ 生产端API封装
 * 1.提供本地消息表能力，业务数据和要发送的MQ消息在同一个事务提交后，再发送MQ消息。保证可靠性。
 * 2.resend方法提供兜底措施，对于没有发送成功的MQ消息进行重发。需要一个分布式定时任务来调度。
 * 3.注意：定时消息存在问题。目前使用apache社区版rocketmq作为MQ设施。其不支持精确的定时消息。现在暂时使用最接近的延时级别来实现。
 * 这会造成定时消息会在发送时间过后某个时间发送。误差会非常大。实时性要求高的需求不建议使用
 */
public class MqSender {

    private static final Logger log = LoggerFactory.getLogger(MqSender.class);

    @Autowired
    MqMessageRepository mqMessageRepository;

    @Autowired
    RocketMQTemplate mqTemplate;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * rocketma的延时级别配置，需要保证跟rocketma配置一致
     */
    @Value("${mqsender.rocketmq.delayLevels:1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h}")
    String delayLevels;

    Duration[] delayDurations;

    /**
     * 多少秒消息还没有发送成功，就重新发送
     */
    @Value("${mqsender.messageTimeout:60}")
    int messageTimeout;

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    protected void init() {
        String[] delayLevelArray = delayLevels.split(" ");
        delayDurations = new Duration[delayLevelArray.length];
        for (int i = 0; i< delayLevelArray.length;i++) {
            delayDurations[i] = Duration.parse("PT"+ delayLevelArray[i]);
        }

    }
    /**
     * 务必在事务中调用该方法！
     * 首先在本地消息表中添加要发送的消息，等事务提交后发送到MQ中！
     * @param topic
     * @param payload
     * @param keys
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void syncSendAfterCommit(String topic, Object payload, String keys) {
        MqMessage mqMessage = saveMqMessage(topic, payload, keys);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
            public void afterCommit() {
                log.debug("事务已经提交，同步发送消息到MQ中！topic:{}, keys: {}, payload: {}", topic, keys, payload);
                Message message = MessageBuilder
                        .withPayload(payload)
                        .setHeader("KEYS", keys)
                        .build();
                SendResult sendResult = mqTemplate.syncSend(topic, message);
                if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                    mqMessageRepository.delete(mqMessage.getId());
                }
            }
        });
    }


    /**
     * 务必在事务中调用该方法！
     * 首先在本地消息表中添加要发送的消息，等事务提交后发送到MQ中！
     * @param topic
     * @param payload
     * @param keys
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void asyncSendAfterCommit(String topic, Object payload, String keys) {
        MqMessage mqMessage = saveMqMessage(topic, payload, keys);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
            public void afterCommit() {
                log.debug("事务已经提交，异步发送消息到MQ中！topic:{}, keys: {}, payload: {}", topic, keys, payload);
                Message message = MessageBuilder
                        .withPayload(payload)
                        .setHeader("KEYS", keys)
                        .build();
                mqTemplate.asyncSend(topic, message, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                            mqMessageRepository.delete(mqMessage.getId());
                        }
                    }

                    @Override
                    public void onException(Throwable e) {

                    }
                });
            }
        });

    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void asyncSendAfterCommit(String topic, Object payload, String keys, LocalDateTime deliverTime) {
        MqMessage mqMessage = saveMqMessage(topic, payload, keys, deliverTime);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
            public void afterCommit() {
                log.debug("事务已经提交，异步发送消息到MQ中！topic:{}, keys: {}, payload: {}", topic, keys, payload);
                Message message = MessageBuilder
                        .withPayload(payload)
                        .setHeader("KEYS", keys)
                        .build();
                mqTemplate.asyncSend(topic, message, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                            mqMessageRepository.delete(mqMessage.getId());
                        }
                    }

                    @Override
                    public void onException(Throwable e) {

                    }
                }, mqTemplate.getProducer().getSendMsgTimeout(),
                        nearestDelayLevel(deliverTime));
            }
        });
    }

    /**
     * 异步发送MQ消息，不存储MQ消息到本地消息表
     * @param topic
     * @param payload
     * @param keys
     */
    public void asyncSend(String topic, Object payload, String keys) {
        Message message = MessageBuilder
                .withPayload(payload)
                .setHeader("KEYS", keys)
                .build();
        mqTemplate.asyncSend(topic, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                if (!SendStatus.SEND_OK.equals(sendResult.getSendStatus()))
                    log.warn("MQ消息发送失败！{}", sendResult);
            }

            @Override
            public void onException(Throwable e) {
                log.warn("MQ消息发送出现异常！{}", e.getMessage());
            }
        });
    }

    /**
     * 重发未发送成功的MQ消息。
     *
     */
    public void resend() {
        log.info("开始重发MQ消息");
        //TODO 考虑极端情况： 1.总是发送失败
        List<MqMessage> mqMessages = mqMessageRepository.getTimeout(messageTimeout, 1000);
        for (MqMessage mqMessage : mqMessages) {
            Message message = null;
            try {
                message = MessageBuilder
                        .withPayload(objectMapper.readValue(mqMessage.getPayload(), JsonNode.class))
                        .setHeader("KEYS", mqMessage.getMessageKeys())
                        .build();
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("无法解析消息");
            }
            SendResult sendResult = null;
            if (mqMessage.getDeliverTime() == null)
                sendResult = mqTemplate.syncSend(mqMessage.getTopic(), message);
            else {
                sendResult = mqTemplate.syncSend(mqMessage.getTopic(), message,
                        mqTemplate.getProducer().getSendMsgTimeout(),
                        nearestDelayLevel(mqMessage.getDeliverTime()));
            }
            if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                mqMessageRepository.delete(mqMessage.getId());
            }
        }
    }

    public RocketMQTemplate getMqTemplate() {
        return mqTemplate;
    }

    public void setMqTemplate(RocketMQTemplate mqTemplate) {
        this.mqTemplate = mqTemplate;
    }

    private int nearestDelayLevel(LocalDateTime deliverTime) {
        Duration delayDuration = Duration.between(LocalDateTime.now(), deliverTime);
        return nearestDelayLevel(delayDuration);
    }

    private int nearestDelayLevel(Duration delayDuration) {
        if (delayDuration == null || delayDuration.isNegative() || delayDuration.isZero())
            return  0;

        for (int i = 0; i < delayDurations.length; i++) {
            if (delayDurations[i].compareTo(delayDuration) >= 0)
                return i+1;
        }
        return delayDurations.length;
    }

    private MqMessage saveMqMessage(String topic, Object message, String keys) {
        return saveMqMessage(topic, message, keys, null);
    }

    private MqMessage saveMqMessage(String topic, Object message, String keys, LocalDateTime deliverTime) {
        try {
            MqMessage mqMessage = new MqMessage();
            mqMessage.setStatus(MqMessage.STATUS_WAIT_SEND);
            mqMessage.setTopic(topic);
            mqMessage.setMessageKeys(keys);
            mqMessage.setDeliverTime(deliverTime);
            String payLoad = objectMapper.writeValueAsString(message);
            mqMessage.setPayload(payLoad);
            mqMessageRepository.create(mqMessage);
            return mqMessage;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("无法序列化消息", e);
        }

    }
}
