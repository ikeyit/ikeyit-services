package com.ikeyit.product.domain;

public class OrderStockLog {
    public static final Integer STATUS_OCCUPIED = 0;
    public static final Integer STATUS_RELEASED = 1;


    Long orderId;
    String content;
    Integer status;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
