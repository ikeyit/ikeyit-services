package com.ikeyit.passport.service.impl;

import com.ikeyit.security.verification.VerificationCode;
import com.ikeyit.security.verification.VerificationCodeSender;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsVerificationCodeSender implements VerificationCodeSender {
    private static Logger log = LoggerFactory.getLogger(SmsVerificationCodeSender.class);

    @Autowired
    RocketMQTemplate mqTemplate;

    @Override
    public boolean send(String destination, VerificationCode verificationCode, String template) {


//        mqTemplate.asyncSend("email", email, new SendCallback() {
//            @Override
//            public void onSuccess(SendResult sendResult) {
//                log.info("发送短信已投递MQ");
//            }
//
//            @Override
//            public void onException(Throwable e) {
//                log.error("发送短信投递MQ失败", e);
//            }
//        });

        return true;
    }
}
