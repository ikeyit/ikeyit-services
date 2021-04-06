package com.ikeyit.message.mq;


import com.ikeyit.message.domain.Email;
import com.ikeyit.message.service.EmailService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RocketMQMessageListener(topic = "email", consumerGroup = "email")
@Component
public class EmailListener implements RocketMQListener<Email> {

    private static final Logger log = LoggerFactory.getLogger(EmailListener.class);

    @Autowired
    EmailService emailService;

    @Override
    public void onMessage(Email email) {
        log.info("[MQ]send email to {}",  email.getMailTo());
        emailService.sendEmail(email);
    }
}
