package com.ikeyit.message.mq;

import java.math.BigDecimal;

/**
 *
 * @author liuzhe
 *
 */
public class OrderMessage {

    private String title;

    private String image;

    private Long quantity;

    private Long buyerId;

    private Long sellerId;

    private Long orderId;

    private Integer orderStatus;

    private BigDecimal totalAmount;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "OrderMessage{" +
                "title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", quantity=" + quantity +
                ", buyerId=" + buyerId +
                ", sellerId=" + sellerId +
                ", orderId=" + orderId +
                ", orderStatus=" + orderStatus +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
