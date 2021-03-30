package com.ikeyit.pay.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单，支付订单跟第三方支付系统的订单一一对应。
 * 不同业务订单，它并不关心具体业务。比如是商品订单还是充值订单。
 */
public class PayOrder {
    public static final Integer STATUS_UNPAID = 0;

    public static final Integer STATUS_PAID = 1;

    public static final Integer STATUS_PAYING = 2;

    private Long id;

    //买家ID
    private Long buyerId;

    //业务订单类型
    private String orderType;

    //业务订单ID
    private Long orderId;

    //支付状态
    private Integer status;

    //交给第三方支付系统的商家订单号，我方的订单号
    private String tradeNo;

    //支付方式
    private String payWay;

    //支付金额
    private BigDecimal paymentAmount;

    //已退款的金额
    private BigDecimal refundAmount;

    //支付内容，比如是xxx商品，还是会员费
    private String subject;

    //第三方支付系统自己的交易流水号
    private String transactionId;

    //第三方支付系统返回的原始报文
    private String transactionData;

    //支付客户端ID
    private String clientIp;

    //过期时间
    private LocalDateTime expireTime;

    private LocalDateTime successTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

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

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
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
