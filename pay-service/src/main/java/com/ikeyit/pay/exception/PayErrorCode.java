package com.ikeyit.pay.exception;

import com.ikeyit.common.exception.ErrorCode;

public enum PayErrorCode implements ErrorCode {
    PAY_ORDER_NOT_FOUND("PAY_ORDER_NOT_FOUND", "支付订单不存在！"),
    PAY_ORDER_EXPIRED("PAY_ORDER_EXPIRED", "支付订单已过期，请重新下单！"),
    PAY_ORDER_PAID("PAY_ORDER_PAID", "订单已支付！");

    private String code;

    private String message;

    PayErrorCode(String code, String message) {
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
