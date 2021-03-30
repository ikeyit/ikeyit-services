package com.ikeyit.product.domain;

import com.ikeyit.common.utils.JsonUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Product {

    public static final Integer STATUS_OFF = 0;

    public static final Integer STATUS_ON = 1;

    public static final Integer STATUS_SOLD_OUT = 2;

    public static final Integer STATUS_DELETED = 4;

    private Long id;

    //店铺ID
    private Long sellerId;

    //产品分类ID
    private Long categoryId;

    //品牌ID
    private Long brandId;

    //产品标题
    private String title;

    //产品副标题
    private String subtitle;

    //产品型号/款号
    private String model;

    //主图
    private String image;

    //橱窗图片
    private String images;

    private String video;

    //产品细节
    private String detail;

    //状态：下架中，上架中，售罄，删除。
    private Integer status;

    //总销量
    private Long sales;

    //推荐度
    private Long recommendation;

    //价格
    private BigDecimal price;

    //促销价
    private BigDecimal promotionPrice;

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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public List<String> getImageUrls() {
        return images == null ?
                Collections.EMPTY_LIST :
                JsonUtils.readValueAsList(images, String.class);
    }

    public void setImageUrls(List<String> imageUrls) {
        if (imageUrls == null)
            this.images = null;
        else
            this.images = JsonUtils.writeValueAsString(imageUrls);
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getSales() {
        return sales;
    }

    public void setSales(Long sales) {
        this.sales = sales;
    }

    public Long getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(Long recommendation) {
        this.recommendation = recommendation;
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
