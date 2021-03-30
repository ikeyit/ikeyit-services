package com.ikeyit.product.domain;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Sku {

    public static final int STATUS_ON = 0;

    public static final int STATUS_OFF = 1;

    public static final int STATUS_DELETED = 4;

    private Long id;

    private Long productId;

    private Long sellerId;

    //状态，启动/不启动
    private Integer status;

    //SKU编码，对应仓库系统里的条形码
    private String code;

    //SKU图片
    private String image;

    //零售价格
    private BigDecimal price;

    //促销价格
    private BigDecimal promotionPrice;

    //库存
    private Long stock;

    //被锁定的库存
    private Long lockedStock;

    //销量
    private Long sales;

    //规格值
    private String attributes;

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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public BigDecimal getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(BigDecimal promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public Long getLockedStock() {
        return lockedStock;
    }

    public void setLockedStock(Long lockedStock) {
        this.lockedStock = lockedStock;
    }

    public Long getSales() {
        return sales;
    }

    public void setSales(Long sales) {
        this.sales = sales;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public List<Long> getAttributeValueIds() {
        return attributes == null ?
                Collections.EMPTY_LIST :
                Arrays.stream(attributes.split(","))
                        .map(item -> Long.parseLong(item))
                        .collect(Collectors.toList());
    }

    public void setAttributeValueIds(List<Long> valueIds) {
        if (valueIds == null || valueIds.isEmpty())
            attributes = null;
        else
            attributes = StringUtils.join(valueIds, ",");
    }
}
