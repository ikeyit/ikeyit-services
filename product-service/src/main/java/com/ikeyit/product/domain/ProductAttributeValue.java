package com.ikeyit.product.domain;

public class ProductAttributeValue {

    Long id;

    Long productId;

    Long attributeValueId;

    Integer position;

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

    public Long getAttributeValueId() {
        return attributeValueId;
    }

    public void setAttributeValueId(Long attributeValueId) {
        this.attributeValueId = attributeValueId;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
