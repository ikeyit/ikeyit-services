package com.ikeyit.passport.dto;

import javax.validation.constraints.NotEmpty;

public class UpdateMobileParam {

    @NotEmpty
    String mobile;

    @NotEmpty
    String code;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
