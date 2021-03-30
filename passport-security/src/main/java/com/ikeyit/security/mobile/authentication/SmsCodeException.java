package com.ikeyit.security.mobile.authentication;

public class SmsCodeException extends RuntimeException {

    public SmsCodeException() {
    }

    public SmsCodeException(String message) {
        super(message);
    }
}
