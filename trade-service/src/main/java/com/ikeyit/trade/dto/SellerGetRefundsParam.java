package com.ikeyit.trade.dto;

public class SellerGetRefundsParam {
    private Integer status;
    private Integer refundType;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRefundType() {
        return refundType;
    }

    public void setRefundType(Integer refundType) {
        this.refundType = refundType;
    }
}
