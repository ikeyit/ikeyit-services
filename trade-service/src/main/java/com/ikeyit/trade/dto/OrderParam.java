package com.ikeyit.trade.dto;

public class OrderParam {

    //收货人地址
    private Long addressId;

    // 支付方式
    private String payWay;

    private String orderToken;

    private String buyerMemo;

    private String source;

    private Long[] cartItemIds;

    private Long skuId;

    private Integer quantity;

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public String getOrderToken() {
        return orderToken;
    }

    public void setOrderToken(String orderToken) {
        this.orderToken = orderToken;
    }

    public String getBuyerMemo() {
        return buyerMemo;
    }

    public void setBuyerMemo(String buyerMemo) {
        this.buyerMemo = buyerMemo;
    }

    public Long[] getCartItemIds() {
        return cartItemIds;
    }

    public void setCartItemIds(Long[] cartItemIds) {
        this.cartItemIds = cartItemIds;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
