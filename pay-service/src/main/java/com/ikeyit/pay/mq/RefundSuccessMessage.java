package com.ikeyit.pay.mq;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RefundSuccessMessage {

    String refundType;

    Long refundId;

    Long payOrderId;

    Long refundOrderId;

    BigDecimal refundAmount;

    String payWay;

    LocalDateTime successTime;

    public String getRefundType() {
        return refundType;
    }

    public void setRefundType(String refundType) {
        this.refundType = refundType;
    }

    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    public Long getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(Long payOrderId) {
        this.payOrderId = payOrderId;
    }

    public Long getRefundOrderId() {
        return refundOrderId;
    }

    public void setRefundOrderId(Long refundOrderId) {
        this.refundOrderId = refundOrderId;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
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
        return "RefundSuccessMessage{" +
                "refundType='" + refundType + '\'' +
                ", refundId=" + refundId +
                ", payOrderId=" + payOrderId +
                ", refundOrderId=" + refundOrderId +
                ", refundAmount=" + refundAmount +
                ", payWay='" + payWay + '\'' +
                ", successTime=" + successTime +
                '}';
    }
}
