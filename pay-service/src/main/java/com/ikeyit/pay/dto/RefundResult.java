package com.ikeyit.pay.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RefundResult {
    private String refundNo;

    private String tradeNo;

    private String transactionId;

    private Integer status;

    private BigDecimal refundAmount;

    private String refundData;

    private LocalDateTime successTime;

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundData() {
        return refundData;
    }

    public void setRefundData(String refundData) {
        this.refundData = refundData;
    }

    public LocalDateTime getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(LocalDateTime successTime) {
        this.successTime = successTime;
    }
}
