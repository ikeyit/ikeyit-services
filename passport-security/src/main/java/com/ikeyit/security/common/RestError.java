package com.ikeyit.security.common;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestError {

    private String errCode;
    private String errMsg;
    private String detail;

    public RestError(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public RestError(String errCode, String errMsg, String detail) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.detail = detail;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
