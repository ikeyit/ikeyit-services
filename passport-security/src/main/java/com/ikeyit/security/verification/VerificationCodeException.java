package com.ikeyit.security.verification;

public class VerificationCodeException extends RuntimeException {

    public VerificationCodeException() {
    }

    public VerificationCodeException(String message) {
        super(message);
    }
}
