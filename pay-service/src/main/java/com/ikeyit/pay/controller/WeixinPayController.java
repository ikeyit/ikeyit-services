package com.ikeyit.pay.controller;

import com.ikeyit.pay.service.impl.wexinpay.WeixinPayProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeixinPayController {

    @Autowired
    WeixinPayProvider weixinPayProvider;

    /**
     * 微信支付回调
     */
    @RequestMapping(value = "/weixinpay_notify")
    public Object onNotify(RequestEntity<String> httpEntity) {
        return weixinPayProvider.handleNotify(httpEntity);
    }

}
