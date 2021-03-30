package com.ikeyit.security.mobile.authentication;


/**
 * 简单的将验证发送到控制台
 */
public class ConsoleSmsCodeSender implements SmsCodeSender {
    @Override
    public boolean sendSms(String mobile, String code) {
        System.out.println("发送验证码给手机号：" + mobile + ", 验证码：" + code);
        return true;
    }
}
