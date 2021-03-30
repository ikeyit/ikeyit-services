package com.ikeyit.product.domain;

public class Attribute {

    public static final Integer TYPE_SALE = 2;
    public static final Integer TYPE_KEY = 1;
    public static final Integer TYPE_BASIC = 0;
    private Long id;

    //属性的名称
    private String name;

    //属性的输入类型： 0 单选, 1 多选，2 默认值，10 手工输入
    private Integer inputType;

    //检索类型： 0 不检索 1 检索
    private Integer searchType;

    //普通属性0/关键属性1/销售属性2
    private Integer attributeType;

    //是否为必填项
    private Boolean required;

    private Boolean obsolete;

    //排序
    private Integer position;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getInputType() {
        return inputType;
    }

    public void setInputType(Integer inputType) {
        this.inputType = inputType;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
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


