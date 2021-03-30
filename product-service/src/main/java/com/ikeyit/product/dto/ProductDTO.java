package com.ikeyit.product.dto;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductDTO {

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
    private List<String> images;

    private String video;

    //产品细节
    private ArrayNode detail;

    //状态：下架中，上架中，售罄，删除。
    private Integer status;

    //总销量
    private Long sales;

    //价格
    private BigDecimal price;

    //促销价
    private BigDecimal promotionPrice;

    List<AttributeDTO> attributes = new ArrayList<>();

    List<AttributeValueDTO> attributeValues = new ArrayList<>();

    List<SkuDTO> skus = new ArrayList<>();

    private LocalDateTime createTime;

    private Long stock;

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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public ArrayNode getDetail() {
        return detail;
    }

    public void setDetail(ArrayNode detail) {
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

    public List<AttributeDTO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeDTO> attributes) {
        this.attributes = attributes;
    }

    public List<AttributeValueDTO> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(List<AttributeValueDTO> attributeValues) {
        this.attributeValues = attributeValues;
    }

    public List<SkuDTO> getSkus() {
        return skus;
    }

    public void setSkus(List<SkuDTO> skus) {
        this.skus = skus;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }
}
