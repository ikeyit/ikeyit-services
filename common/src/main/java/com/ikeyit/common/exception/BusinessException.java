package com.ikeyit.common.exception;

import java.text.MessageFormat;

public class BusinessException extends RuntimeException{

    ErrorCode errorCode;

    Object[] params;

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, null, (Object[])null);
    }

    public BusinessException(ErrorCode errorCode, Object... params) {
        this(errorCode, null, params);
    }

    public BusinessException(ErrorCode errorCode, Throwable cause, Object... params) {
        super(MessageFormat.format(errorCode.getMessage(), (Object[]) params), cause);
        this.errorCode = errorCode;
        this.params = params;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
}
