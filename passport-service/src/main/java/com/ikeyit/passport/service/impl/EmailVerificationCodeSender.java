package com.ikeyit.passport.service.impl;

import com.ikeyit.message.domain.Email;
import com.ikeyit.security.verification.VerificationCode;
import com.ikeyit.security.verification.VerificationCodeSender;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * 通过邮件发送验证码，发送到MQ
 */
@Service
public class EmailVerificationCodeSender implements VerificationCodeSender {

    private static Logger log = LoggerFactory.getLogger(EmailVerificationCodeSender.class);

    @Autowired
    RocketMQTemplate mqTemplate;

    @Override
    public boolean send(String destination, VerificationCode verificationCode, String template) {
        Email email = new Email();
        email.setFrom("ikeyit");
        email.setMailTo(new String[]{destination});
        email.setSubject("邮箱验证");
        email.setTemplate("update_email");
        email.getModel().put("code", verificationCode.getCode());
        email.getModel().put("expireTime",  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(verificationCode.getExpireTime()));
        mqTemplate.asyncSend("email", email, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("发送邮件已投递MQ");
            }

            @Override
            public void onException(Throwable e) {
                log.error("发送邮件投递MQ失败", e);
            }
        });

        return true;
    }
}
