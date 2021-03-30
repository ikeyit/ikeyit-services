package com.ikeyit.pay.service.impl.wexinpay;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface WeixinPayListener {
    void onNotify(String eventType, ObjectNode notification, String notificationStr);
}
