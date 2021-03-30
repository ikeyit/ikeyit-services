package com.ikeyit.pay.service;

import com.ikeyit.pay.domain.PayOrder;
import com.ikeyit.pay.domain.RefundOrder;
import com.ikeyit.pay.dto.CreatePayOrderParam;
import com.ikeyit.pay.dto.CreateRefundOrderParam;
import com.ikeyit.pay.dto.SubmitPayOrderParam;

public interface PayService {

    Long createPayOrder(CreatePayOrderParam createPayOrderParam);

    Object submitPayOrder(SubmitPayOrderParam submitPayOrderParam);

    PayOrder getPayOrder(String orderType, Long orderId);

    void syncPayResult(String orderType, Long orderId, String payWay);

    Long createRefundOrder(CreateRefundOrderParam createRefundOrderParam);

    void submitRefundOrder(String refundType, Long refundId);

    RefundOrder getRefundOrder(String refundType, Long refundId);
}
