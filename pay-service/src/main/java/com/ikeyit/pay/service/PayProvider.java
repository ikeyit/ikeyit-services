package com.ikeyit.pay.service;

import com.ikeyit.pay.domain.PayOrder;
import com.ikeyit.pay.domain.RefundOrder;
import com.ikeyit.pay.dto.PayResult;
import com.ikeyit.pay.dto.RefundResult;

import java.util.Map;

public interface PayProvider {

    String getName();

    Object requestPayment(PayOrder payOrder, Map<String, Object> params);

    PayResult queryPayment(String trade_no);

    void requestRefund(RefundOrder refundOrder);

    RefundResult queryRefund(String refund_no);

    void setPayCallback(PayCallback payCallback);
}
