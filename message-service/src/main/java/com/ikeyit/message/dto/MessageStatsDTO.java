package com.ikeyit.message.dto;

public class MessageStatsDTO {
    public MessageStatsDTO(Integer messageType, long unreadCount) {
        this.messageType = messageType;
        this.unreadCount = unreadCount;
    }

    private Integer messageType;

    private long unreadCount;

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
