package com.ikeyit.product.domain;

public class ShopCategoryProduct {

    Long id;

    Long productId;

    Long shopCategoryId1;

    Long shopCategoryId2;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getShopCategoryId1() {
        return shopCategoryId1;
    }

    public void setShopCategoryId1(Long shopCategoryId1) {
        this.shopCategoryId1 = shopCategoryId1;
    }

    public Long getShopCategoryId2() {
        return shopCategoryId2;
    }

    public void setShopCategoryId2(Long shopCategoryId2) {
        this.shopCategoryId2 = shopCategoryId2;
    }
}
