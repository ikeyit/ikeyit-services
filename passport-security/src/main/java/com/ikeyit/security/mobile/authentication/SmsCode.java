package com.ikeyit.security.mobile.authentication;

import java.time.LocalDateTime;

public class SmsCode {

    private String mobile;

    private String code;

    private LocalDateTime expireTime;

    private LocalDateTime sendTime;

    public SmsCode(String mobile, String code) {
        this.mobile = mobile;
        this.code = code;
    }

    public SmsCode(String mobile, String code, LocalDateTime expireTime) {
        this(mobile, code, expireTime, LocalDateTime.now());
    }

    public SmsCode(String mobile, String code, LocalDateTime expireTime, LocalDateTime sendTime) {
        this.mobile = mobile;
        this.code = code;
        this.expireTime = expireTime;
        this.sendTime = sendTime;
    }

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

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }
}
