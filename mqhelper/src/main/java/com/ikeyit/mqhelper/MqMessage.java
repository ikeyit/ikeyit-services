package com.ikeyit.mqhelper;

import java.time.LocalDateTime;

public class MqMessage {
    public static final Integer STATUS_WAIT_SEND = 0;
    public static final Integer STATUS_FAIL = 1;

    Long id;

    Integer status;

    String topic;

    String payload;

    String messageKeys;

    LocalDateTime deliverTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getMessageKeys() {
        return messageKeys;
    }

    public void setMessageKeys(String messageKeys) {
        this.messageKeys = messageKeys;
    }

    public LocalDateTime getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(LocalDateTime deliverTime) {
        this.deliverTime = deliverTime;
    }
}
