package com.ikeyit.security.verification;


/**
 * 简单的将验证发送到控制台
 */
public class ConsoleVerificationCodeSender implements VerificationCodeSender {


    @Override
    public boolean send(String destination, VerificationCode verificationCode, String template) {
        System.out.println("发送验证码给：" + destination + ", 验证码：" + verificationCode.getCode() + ", 模板：" + template);
        return true;
    }
}
