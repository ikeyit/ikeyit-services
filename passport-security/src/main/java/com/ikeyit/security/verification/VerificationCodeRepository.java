package com.ikeyit.security.verification;


public interface VerificationCodeRepository {

    void save(VerificationCode verificationCode);

    VerificationCode getByTarget(String target);

    void deleteByTarget(String target);
}
