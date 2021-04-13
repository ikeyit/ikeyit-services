package com.ikeyit.passport.dto;

import javax.validation.constraints.Email;

public class SendEmailVerificationCodeParam {


    @Email
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
