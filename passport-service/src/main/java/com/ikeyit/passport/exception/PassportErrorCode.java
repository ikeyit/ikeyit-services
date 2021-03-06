package com.ikeyit.passport.exception;

import com.ikeyit.common.exception.ErrorCode;

public enum PassportErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", "用户不存在"),
    MOBILE_EXISTS ("MOBILE_EXISTS", "该手机号已被使用"),
    EMAIL_EXISTS ("EMAIL_EXISTS", "该邮箱已被使用"),
    BAD_VERIFICATION_CODE("BAD_VERIFICATION_CODE", "验证码错误"),
    WRONG_PASSWORD("WRONG_PASSWORD", "输入的密码不正确"),
    BAD_FORMAT_PASSWORD("BAD_FORMAT_PASSWORD", "密码格式不正确,长度至少为6位，且不能包含用户名，手机和旧密码"),
    WRONG_VERIFICATION_CODE("WRONG_VERIFICATION_CODE", "验证码不正确");

    private String code;

    private String message;

    PassportErrorCode(String code, String message) {
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
