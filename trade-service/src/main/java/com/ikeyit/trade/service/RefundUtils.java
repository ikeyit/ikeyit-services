package com.ikeyit.trade.service;

import com.ikeyit.common.utils.JsonUtils;
import com.ikeyit.trade.domain.RefundNegotiation;
import com.ikeyit.trade.dto.RefundNegotiationDTO;

import java.util.LinkedHashMap;

public class RefundUtils {
    public static RefundNegotiationDTO convert(RefundNegotiation refundNegotiation) {
        RefundNegotiationDTO refundNegotiationDTO = new RefundNegotiationDTO();
        refundNegotiationDTO.setOperator(refundNegotiation.getOperator());
        refundNegotiationDTO.setOperatorId(refundNegotiation.getOperatorId());
        refundNegotiationDTO.setOperation(refundNegotiation.getOperation());
        refundNegotiationDTO.setMessage(JsonUtils.readValue(refundNegotiation.getMessage(), LinkedHashMap.class));
        refundNegotiationDTO.setCreateTime(refundNegotiation.getCreateTime());
        return refundNegotiationDTO;
    }
}
