package com.ikeyit.trade.dto;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

public class RefundNegotiationDTO {

    String operator;

    Long operatorId;

    String operation;

    LinkedHashMap message;

    LocalDateTime createTime;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public LinkedHashMap getMessage() {
        return message;
    }

    public void setMessage(LinkedHashMap message) {
        this.message = message;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
