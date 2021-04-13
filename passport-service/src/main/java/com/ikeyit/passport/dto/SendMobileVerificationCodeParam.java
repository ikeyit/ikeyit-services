package com.ikeyit.passport.dto;

import javax.validation.constraints.Email;

public class SendMobileVerificationCodeParam {

    @Email
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
