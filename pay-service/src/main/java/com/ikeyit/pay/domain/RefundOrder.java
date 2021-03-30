package com.ikeyit.pay.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款订单，跟第三方支付系统里的退款订单一一对应。
 * 它并不关心具体业务实现。
 */
public class RefundOrder {
    public static final Integer STATUS_CREATED = 0;

    public static final Integer STATUS_SUBMITTED = 1;

    public static final Integer STATUS_SUCCESS = 2;

    private Long id;

    //支付订单ID
    private Long payOrderId;

    //退款业务类型
    private String refundType;

    //业务退款单号
    private Long refundId;

    //状态
    private Integer status;

    //交给第三方支付系统的商家退款编号，我方的编号
    private String refundNo;

    //交给第三方支付系统的商家订单编号，我方的编号
    private String tradeNo;

    //退款原因
    private String reason;

    //支付方式
    private String payWay;

    //退款金额
    private BigDecimal refundAmount;

    //支付总金额
    private BigDecimal paymentAmount;

    //第三方原始退款订单报文
    private String refundData;

    private LocalDateTime successTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(Long payOrderId) {
        this.payOrderId = payOrderId;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
