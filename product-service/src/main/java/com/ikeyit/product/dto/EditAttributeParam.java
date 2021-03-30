package com.ikeyit.product.dto;

import java.util.List;

public class EditAttributeParam {

    public static class AttributeValueParam {

        private Long valueId;

        private String val;

        private Boolean deleted;

        public Long getValueId() {
            return valueId;
        }

        public void setValueId(Long valueId) {
            this.valueId = valueId;
        }

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return "AttributeValueParam{" +
                    "id=" + valueId +
                    ", val='" + val + '\'' +
                    '}';
        }

        public Boolean getDeleted() {
            return deleted;
        }

        public void setDeleted(Boolean deleted) {
            this.deleted = deleted;
        }
    }

    private Long attributeId;

    private String name;

    private Integer attributeType = 0;

    private Boolean required = Boolean.FALSE;

    private List<AttributeValueParam> values;

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<AttributeValueParam> getValues() {
        return values;
    }

    public void setValues(List<AttributeValueParam> values) {
        this.values = values;
    }
}
