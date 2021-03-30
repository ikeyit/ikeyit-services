package com.ikeyit.pay.service;

import java.util.List;

public interface PayProviderManager {

    void setPayProviders(List<PayProvider> providers);

    PayProvider get(String payWay);
}
