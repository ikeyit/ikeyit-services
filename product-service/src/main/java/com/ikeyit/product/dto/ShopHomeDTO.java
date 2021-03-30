package com.ikeyit.product.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class ShopHomeDTO {

    private String shopName;

    private String shopDescription;

    private Long shopId;

    private JsonNode content;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public JsonNode getContent() {
        return content;
    }

    public void setContent(JsonNode content) {
        this.content = content;
    }

    public String getShopDescription() {
        return shopDescription;
    }

    public void setShopDescription(String shopDescription) {
        this.shopDescription = shopDescription;
    }
}
