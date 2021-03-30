package com.ikeyit.product.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class UpdateShopPageParam {
    private Long id;

    private String name;

    private Integer status;

    private Integer version;

    private JsonNode content;

    private Boolean preferred;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public JsonNode getContent() {
        return content;
    }

    public void setContent(JsonNode content) {
        this.content = content;
    }

    public Boolean getPreferred() {
        return preferred;
    }

    public void setPreferred(Boolean preferred) {
        this.preferred = preferred;
    }
}
