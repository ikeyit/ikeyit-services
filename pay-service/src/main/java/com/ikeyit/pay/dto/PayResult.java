package com.ikeyit.pay.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PayResult {

    public static final Integer STATUS_SUCCESS = 1;

    public static final Integer STATUS_UNPAID = 0;

    private Integer status;

    private String tradeNo;

    private String payWay;

    private BigDecimal paymentAmount;

    private String transactionId;

    private String transactionData;

    private LocalDateTime successTime;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionData() {
        return transactionData;
    }

    public void setTransactionData(String transactionData) {
        this.transactionData = transactionData;
    }

    public LocalDateTime getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(LocalDateTime successTime) {
        this.successTime = successTime;
    }
}
