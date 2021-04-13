package com.ikeyit.passport.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SetUserMobileParam {

    @NotNull
    Long userId;

    @NotEmpty
    String mobile;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
