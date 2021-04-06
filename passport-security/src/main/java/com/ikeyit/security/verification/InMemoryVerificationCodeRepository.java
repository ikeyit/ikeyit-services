package com.ikeyit.security.verification;

import java.util.HashMap;

public class InMemoryVerificationCodeRepository implements VerificationCodeRepository {

    HashMap<String, VerificationCode> codes = new HashMap<>();

    public InMemoryVerificationCodeRepository() {

    }

    @Override
    public void save(VerificationCode verificationCode) {
        codes.put(verificationCode.getTarget(), verificationCode);
    }

    @Override
    public VerificationCode getByTarget(String target) {
        return codes.get(target);
    }


    @Override
    public void deleteByTarget(String target) {
        codes.remove(target);
    }
}
