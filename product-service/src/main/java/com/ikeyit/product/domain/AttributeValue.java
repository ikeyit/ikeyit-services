package com.ikeyit.product.domain;

public class AttributeValue {
    private Long id;

    //属性ID
    private Long attributeId;

    //属性值
    private String val;

    private Integer position;

    private Long productId;

    private Boolean obsolete;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Boolean getObsolete() {
        return obsolete;
    }

    public void setObsolete(Boolean obsolete) {
        this.obsolete = obsolete;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
