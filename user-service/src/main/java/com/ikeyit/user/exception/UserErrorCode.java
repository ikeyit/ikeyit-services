package com.ikeyit.user.exception;

import com.ikeyit.common.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", "用户不存在");

    private String code;

    private String message;

    UserErrorCode(String code, String message) {
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
