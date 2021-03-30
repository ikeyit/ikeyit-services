package com.ikeyit.trade.domain;

import java.time.LocalDateTime;

public class SellerAddress {
    private Long id;

    private Long sellerId;

    private String name;

    private String phone;

    private String province;

    private String city;

    private String district;

    private String street;

    private String zipCode;

    private Boolean defaultShipFrom = Boolean.FALSE;

    private Boolean defaultReturnTo = Boolean.FALSE;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Boolean getDefaultShipFrom() {
        return defaultShipFrom;
    }

    public void setDefaultShipFrom(Boolean defaultShipFrom) {
        this.defaultShipFrom = defaultShipFrom;
    }

    public Boolean getDefaultReturnTo() {
        return defaultReturnTo;
    }

    public void setDefaultReturnTo(Boolean defaultReturnTo) {
        this.defaultReturnTo = defaultReturnTo;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}


