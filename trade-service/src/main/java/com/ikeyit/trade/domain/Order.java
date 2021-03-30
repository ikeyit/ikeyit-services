package com.ikeyit.trade.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {

    public static Integer STATUS_WAIT_BUYER_PAY = 10;

    public static Integer STATUS_CLOSED = 20;

    public static Integer STATUS_PAID = 30;

//    public static Integer STATUS_WAIT_GROUP = 40;

    public static Integer STATUS_SHIPPED = 70;

//    public static Integer STATUS_CONFIRMED = 80;

    public static Integer STATUS_FINISHED = 90;

    public static Integer CLOSE_REASON_EXPIRED = 1;

    public static Integer CLOSE_REASON_BUYER_CANCELLED = 2;

    public static Integer CLOSE_REASON_REFUNDED = 3;

    Long id;

    Integer status;

    Integer closeReason;


    //买方信息
    Long buyerId;

    String buyerName;

    String buyerMemo;

    //卖方信息
    Long sellerId;

    String sellerMemo;

    Integer orderType;

    //价格信息

    //货品总数量
    Long goodsQuantity;

    //货款金额 = 所有商品单价 * 商品数量之和
    BigDecimal goodsAmount;

    //支付金额 = 货款金额 + 邮费 - 折扣金额
    BigDecimal paymentAmount;

    //折扣金额 = 各种折扣的总计金额
    BigDecimal discountAmount;

    //邮费
    BigDecimal freightAmount;


    //收货人信息
    String receiverName;

    String receiverPhone;

    String receiverProvince;

    String receiverCity;

    String receiverDistrict;

    String receiverStreet;

    //发票信息
    String invoiceTitle;

    String invoiceContent;

    String invoicePayerTaxId;


    //支付方式：微信/支付宝/
    String payWay;
    
    //支付流水号
    Long payOrderId;

    LocalDateTime payTime;

    LocalDateTime expireTime;

    //订单来源
    String source ;


    String logisticsCompany;

    String trackingNumber;

    LocalDateTime shipTime;

    LocalDateTime createTime;

    LocalDateTime updateTime;

    LocalDateTime finishTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCloseReason() {
        return closeReason;
    }

    public void setCloseReason(Integer closeReason) {
        this.closeReason = closeReason;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerMemo() {
        return buyerMemo;
    }

    public void setBuyerMemo(String buyerMemo) {
        this.buyerMemo = buyerMemo;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerMemo() {
        return sellerMemo;
    }

    public void setSellerMemo(String sellerMemo) {
        this.sellerMemo = sellerMemo;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Long getTotalQuantity() {
        return goodsQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.goodsQuantity = totalQuantity;
    }

    public BigDecimal getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(BigDecimal goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFreightAmount() {
        return freightAmount;
    }

    public void setFreightAmount(BigDecimal freightAmount) {
        this.freightAmount = freightAmount;
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

    public String getReceiverProvince() {
        return receiverProvince;
    }

    public void setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getReceiverDistrict() {
        return receiverDistrict;
    }

    public void setReceiverDistrict(String receiverDistrict) {
        this.receiverDistrict = receiverDistrict;
    }

    public String getReceiverStreet() {
        return receiverStreet;
    }

    public void setReceiverStreet(String receiverStreet) {
        this.receiverStreet = receiverStreet;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public String getInvoiceContent() {
        return invoiceContent;
    }

    public void setInvoiceContent(String invoiceContent) {
        this.invoiceContent = invoiceContent;
    }

    public String getInvoicePayerTaxId() {
        return invoicePayerTaxId;
    }

    public void setInvoicePayerTaxId(String invoicePayerTaxId) {
        this.invoicePayerTaxId = invoicePayerTaxId;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public Long getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(Long payOrderId) {
        this.payOrderId = payOrderId;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public String getLogisticsCompany() {
        return logisticsCompany;
    }

    public void setLogisticsCompany(String logisticsCompany) {
        this.logisticsCompany = logisticsCompany;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public LocalDateTime getShipTime() {
        return shipTime;
    }

    public void setShipTime(LocalDateTime shipTime) {
        this.shipTime = shipTime;
    }
}
