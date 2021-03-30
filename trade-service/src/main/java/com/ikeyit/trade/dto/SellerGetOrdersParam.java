package com.ikeyit.trade.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class SellerGetOrdersParam {
    Integer status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime createTimeStart;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime createTimeEnd;

    Long buyerId;

    Long orderId;

    String receiverName;

    String receiverPhone;

    String trackingNumber;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(LocalDateTime createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public LocalDateTime getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(LocalDateTime createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
}
