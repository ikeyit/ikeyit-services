package com.ikeyit.product.dto;

public class RemoveProductIntoShopCategoryParam {

    Long shopCategoryId;

    Long[] productIds;

    public Long getShopCategoryId() {
        return shopCategoryId;
    }

    public void setShopCategoryId(Long shopCategoryId) {
        this.shopCategoryId = shopCategoryId;
    }

    public Long[] getProductIds() {
        return productIds;
    }

    public void setProductIds(Long[] productIds) {
        this.productIds = productIds;
    }
}
