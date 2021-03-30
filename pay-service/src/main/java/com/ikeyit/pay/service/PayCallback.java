package com.ikeyit.pay.service;

import com.ikeyit.pay.dto.PayResult;
import com.ikeyit.pay.dto.RefundResult;

public interface PayCallback {
    void handlePaySuccess(PayResult payResult);

    void handleRefundSuccess(RefundResult refundResult);
}
