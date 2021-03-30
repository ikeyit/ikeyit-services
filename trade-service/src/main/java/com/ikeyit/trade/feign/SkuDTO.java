package com.ikeyit.trade.feign;

import java.math.BigDecimal;
import java.util.List;

public class SkuDTO {
    Long id;
    Long productId;
    Long sellerId;
    Integer stock;
    String title;
    String image;
    BigDecimal price;
    List<AttributeValueDTO> attributes;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public List<AttributeValueDTO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeValueDTO> attributes) {
        this.attributes = attributes;
    }
}
