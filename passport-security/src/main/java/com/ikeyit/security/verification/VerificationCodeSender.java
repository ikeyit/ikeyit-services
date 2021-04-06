package com.ikeyit.security.verification;

public interface VerificationCodeSender {

    boolean send(String destination, VerificationCode verificationCode, String template);
}
