package com.ikeyit.pay.service.impl;

import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.pay.service.PayProvider;
import com.ikeyit.pay.service.PayProviderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayProviderManagerImpl implements PayProviderManager {

    Map<String, PayProvider> payProviders = new HashMap<>();

    @Autowired
    @Override
    public void setPayProviders(List<PayProvider> providers) {
        for (PayProvider provider : providers) {
            payProviders.put(provider.getName(), provider);
        }
    }


    @Override
    public PayProvider get(String payWay) {
        PayProvider payProvider = payProviders.get(payWay);
        if (payProvider == null)
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR, "支付方式不存在！");
        return payProvider;
    }
}
