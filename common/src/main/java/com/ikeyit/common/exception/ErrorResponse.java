package com.ikeyit.common.exception;

public class ErrorResponse {

    private String errCode;

    private String errMsg;

    public ErrorResponse() {
    }

    public ErrorResponse(String errCode) {
        this.errCode = errCode;
    }

    public ErrorResponse(String errCode, String errMessage) {
        this.errCode = errCode;
        this.errMsg = errMessage;
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

}
