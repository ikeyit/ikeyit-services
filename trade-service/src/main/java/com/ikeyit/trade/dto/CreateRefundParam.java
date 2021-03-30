package com.ikeyit.trade.dto;

import java.math.BigDecimal;

public class CreateRefundParam {
    Long orderItemId;

    Integer refundType;

    String reason;

    String memo;

    BigDecimal amount;

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getRefundType() {
        return refundType;
    }

    public void setRefundType(Integer refundType) {
        this.refundType = refundType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
