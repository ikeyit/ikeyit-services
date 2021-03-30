package com.ikeyit.pay.mq;

public class RefundSubmitMessage {
    String refundType;
    Long refundId;

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

    @Override
    public String toString() {
        return "RefundSubmitMessage{" +
                "refundType='" + refundType + '\'' +
                ", refundId=" + refundId +
                '}';
    }
}
