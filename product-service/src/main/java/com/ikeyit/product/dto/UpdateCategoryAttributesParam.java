package com.ikeyit.product.dto;

import java.util.List;

public class UpdateCategoryAttributesParam {
    public static class AttributeParam {
        Long attributeId;

        public Long getAttributeId() {
            return attributeId;
        }

        public void setAttributeId(Long attributeId) {
            this.attributeId = attributeId;
        }
    }

    Long id;

    List<AttributeParam> attributes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<AttributeParam> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeParam> attributes) {
        this.attributes = attributes;
    }
}
