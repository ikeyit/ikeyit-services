package com.ikeyit.security.mobile.authentication;

import java.util.HashMap;

public class InMemeorySmsCodeRepository implements SmsCodeRepository {

    HashMap<String, SmsCode> smsCodes = new HashMap<>();

    public InMemeorySmsCodeRepository() {

    }

    @Override
    public void save(SmsCode smsCode) {
        smsCodes.put(smsCode.getMobile(), smsCode);
    }

    @Override
    public SmsCode getByMobile(String mobile) {
        return smsCodes.get(mobile);
    }
}
