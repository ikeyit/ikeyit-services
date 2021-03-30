package com.ikeyit.pay.mq;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaySuccessMessage {

    String orderType;

    Long orderId;

    Long payOrderId;

    BigDecimal paymentAmount;

    String payWay;
//
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime successTime;

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(Long payOrderId) {
        this.payOrderId = payOrderId;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public LocalDateTime getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(LocalDateTime successTime) {
        this.successTime = successTime;
    }

    @Override
    public String toString() {
        return "PaySuccessMessage{" +
                "orderType='" + orderType + '\'' +
                ", orderId=" + orderId +
                ", payOrderId=" + payOrderId +
                ", paymentAmount=" + paymentAmount +
                ", payWay='" + payWay + '\'' +
                ", successTime=" + successTime +
                '}';
    }
}
