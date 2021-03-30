package com.ikeyit.security.mobile.authentication;


public interface SmsCodeRepository {

    void save(SmsCode code);

    SmsCode getByMobile(String mobile);

}
