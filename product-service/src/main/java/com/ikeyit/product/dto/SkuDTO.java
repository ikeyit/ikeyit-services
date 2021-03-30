package com.ikeyit.product.dto;

import java.math.BigDecimal;
import java.util.List;

public class SkuDTO {
    Long id;
    Long productId;
    Long sellerId;
    Long stock;
    String title;
    String name;
    String image;
    BigDecimal price;
    List<AttributeValueDTO> attributes;
    List<Long> attributeValueIds;
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

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public List<AttributeValueDTO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeValueDTO> attributes) {
        this.attributes = attributes;
    }

    public List<Long> getAttributeValueIds() {
        return attributeValueIds;
    }

    public void setAttributeValueIds(List<Long> attributeValueIds) {
        this.attributeValueIds = attributeValueIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
