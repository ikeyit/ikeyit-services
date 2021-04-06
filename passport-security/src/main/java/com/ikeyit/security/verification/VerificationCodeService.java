package com.ikeyit.security.verification;

public interface VerificationCodeService {

    public VerificationCode generateCode(String key);

    public VerificationCode validate(String key, String code) throws VerificationCodeException;
}
