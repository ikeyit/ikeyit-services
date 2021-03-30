package com.ikeyit.pay.service.impl.wexinpay;

import java.util.Map;

public interface WeixinPayListenerV2 {
    void handleNotification(Map<String, String> data);
}
