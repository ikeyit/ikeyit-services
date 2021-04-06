package com.ikeyit.passport.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class SendEmailVerificationParam {

    @Email
    @NotEmpty
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
