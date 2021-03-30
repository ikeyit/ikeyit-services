package com.ikeyit.product.exception;

import com.ikeyit.common.exception.ErrorCode;

public enum ProductErrorCode implements ErrorCode {

    PRODUCT_ILLEGAL_STATUS("PRODUCT_ILLEGAL_STATUS", "产品状态不正确"),

    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "产品不存在{0}"),

    SKU_NOT_FOUND("SKU_NOT_FOUND", "SKU不存在{0}"),

    OUT_OF_STOCK("OUT_OF_STOCK", "SKU库存不足{0}");

    private String code;

    private String message;

    ProductErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
