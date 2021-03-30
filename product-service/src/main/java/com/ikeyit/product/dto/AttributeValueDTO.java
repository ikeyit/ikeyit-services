package com.ikeyit.product.dto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AttributeValueDTO {

    private Long attributeId;

    private String name;

    private Long valueId;

    private String val;

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

    public static List<AttributeValueDTO> fromIdsStr(String idsStr) {
        if (idsStr == null || idsStr.isEmpty())
            return null;
        return Arrays.stream(idsStr.split(",")).map(item->{
            String[] ids = item.split(":");
            AttributeValueDTO attributeDTO = new AttributeValueDTO();
            attributeDTO.setValueId(Long.parseLong(ids[1]));
            attributeDTO.setAttributeId(Long.parseLong(ids[0]));
            return attributeDTO;
        }).collect(Collectors.toList());
    }

}
