package com.ikeyit.trade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    private Long id;

    private Integer status;

    private Long buyerId;

    private String buyerNick;

    private String buyerMemo;

    private Long sellerId;

    private String sellerMemo;

    private List<OrderItemDTO> items;

    private String receiverName;

    private String receiverPhone;

    private String receiverProvince;

    private String receiverCity;

    private String receiverDistrict;

    private String receiverStreet;

    private Long goodsQuantity;

    private BigDecimal goodsAmount;

    private BigDecimal paymentAmount;

    private BigDecimal discountAmount;

    private BigDecimal freightAmount;

    String logisticsCompany;

    String trackingNumber;

    LocalDateTime shipTime;

    LocalDateTime createTime;

    LocalDateTime finishTime;

    LocalDateTime payTime;

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

    public String getBuyerNick() {
        return buyerNick;
    }

    public void setBuyerNick(String buyerNick) {
        this.buyerNick = buyerNick;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getBuyerMemo() {
        return buyerMemo;
    }

    public void setBuyerMemo(String buyerMemo) {
        this.buyerMemo = buyerMemo;
    }

    public String getSellerMemo() {
        return sellerMemo;
    }

    public void setSellerMemo(String sellerMemo) {
        this.sellerMemo = sellerMemo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
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

    public Long getGoodsQuantity() {
        return goodsQuantity;
    }

    public void setGoodsQuantity(Long goodsQuantity) {
        this.goodsQuantity = goodsQuantity;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
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
}
