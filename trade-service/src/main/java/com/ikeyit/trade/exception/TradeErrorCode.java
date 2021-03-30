package com.ikeyit.trade.exception;

import com.ikeyit.common.exception.ErrorCode;

public enum TradeErrorCode implements ErrorCode {

    CART_ITEM_NOT_FOUND("CART_ITEM_NOT_FOUND", "没有找到购物车项{0}"),

    CART_ITEM_ILLEGAL_ACCESS("CART_ITEM_ILLEGAL_ACCESS", "非法访问购物车"),

    ORDER_INVALID_STATUS("ORDER_ILLEGAL_STATUS", "订单{0}状态不正确，无法执行该操作"),

    ORDER_ILLEGAL_ACCESS("ORDER_ILLEGAL_ACCESS", "非法访问订单{0}"),

    ORDER_NOT_FOUND("ORDER_NOT_FOUND", "订单{0}不存在"),

    REFUND_ILLEGAL_ACCESS("REFUND_ILLEGAL_ACCESS", "非法访问退款单{0}"),

    REFUND_NOT_FOUND("REFUND_NOT_FOUND", "退款单{0}不存在"),

    REFUND_INVALID_STATUS("REFUND_INVALID_STATUS", "退款单{0}状态不正确，无法执行才操作"),

    REFUND_EXISTING("REFUND_EXISTING", "退款单{0}已经存在");

    private String code;

    private String message;

    TradeErrorCode(String code, String message) {
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
