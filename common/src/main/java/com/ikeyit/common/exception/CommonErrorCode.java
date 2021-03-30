package com.ikeyit.common.exception;

public enum CommonErrorCode implements ErrorCode {

    BAD_REQUEST("BAD_REQUEST", "无效请求"),

    INVALID_ARGUMENT("INVALID_ARGUMENT", "无效参数!{0}"),

    FORBIDDEN("FORBIDDEN", "没有足够的权限，禁止访问"),

    UNAUTHORIZED("UNAUTHORIZED", "需要认证"),

    NOT_FOUND("NOT_FOUND","请求路径{0}不存在"),

    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND","请求的资源{0}不存在"),

    RESOURCE_UPDATE_CONFLICT("RESOURCE_UPDATE_CONFLICT","更新资源冲突，可能已经被别人修改"),

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "服务器内部错误!{0}"),

    THIRD_PARTY_ERROR("THIRD_PARTY_ERROR", "{0}");

    private String code;

    private String message;

    CommonErrorCode(String code, String message) {
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
