package com.ikeyit.security.mobile.authentication;

public interface SmsCodeService {

    public void sendCode(String mobile) throws SmsCodeException;

    public SmsCode getCode(String mobile);

    public void validate(String mobile, String code) throws SmsCodeException;
}
