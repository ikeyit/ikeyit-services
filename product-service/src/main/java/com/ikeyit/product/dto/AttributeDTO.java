package com.ikeyit.product.dto;

import java.util.List;

public class AttributeDTO implements Comparable<AttributeDTO> {

    private Long attributeId;

    private String name;

    private Integer position;

    //普通属性0/关键属性1/销售属性2
    private Integer attributeType;

    //是否为必填项
    private Boolean required;

    private List<AttributeValueDTO> values;

    public AttributeDTO() {
    }

    public AttributeDTO(Long attributeId, String name, Integer position) {
        this.attributeId = attributeId;
        this.name = name;
        this.position = position;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AttributeValueDTO> getValues() {
        return values;
    }

    public void setValues(List<AttributeValueDTO> values) {
        this.values = values;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(Integer attributeType) {
        this.attributeType = attributeType;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Override
    public int compareTo(AttributeDTO o) {
        return this.position - o.position;
    }
}
