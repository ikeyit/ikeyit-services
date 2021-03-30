package com.ikeyit.product.dto;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.math.BigDecimal;
import java.util.List;

public class EditProductParam {

    public static class AttributeParam {

        Long valueId;

        Long attributeId;

        String val;

        public Long getValueId() {
            return valueId;
        }

        public void setValueId(Long valueId) {
            this.valueId = valueId;
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
    }


    public static class SkuParam {
        Long id;
        BigDecimal price;
        Long stock;
        String code;
        String image;
        List<AttributeParam> attributes;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Long getStock() {
            return stock;
        }

        public void setStock(Long stock) {
            this.stock = stock;
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

        public List<AttributeParam> getAttributes() {
            return attributes;
        }

        public void setAttributes(List<AttributeParam> attributes) {
            this.attributes = attributes;
        }
    }

    private Long id;

    private Long categoryId;

    private String title;

    private String subtitle;

    private String model;

    private List<String> images;

    private String video;

    private ArrayNode detail;

    private List<AttributeParam> attributes;

    private List<SkuParam> skus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public List<AttributeParam> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeParam> attributes) {
        this.attributes = attributes;
    }

    public List<SkuParam> getSkus() {
        return skus;
    }

    public void setSkus(List<SkuParam> skus) {
        this.skus = skus;
    }

    public ArrayNode getDetail() {
        return detail;
    }

    public void setDetail(ArrayNode detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "EditProductParam{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", model='" + model + '\'' +
                ", images=" + images +
                ", video='" + video + '\'' +
                ", attributes=" + attributes +
                ", skus=" + skus +
                '}';
    }
}
