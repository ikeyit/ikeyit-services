package com.ikeyit.trade.repository;


import com.ikeyit.trade.domain.RefundNegotiation;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RefundNegotiationRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT INTO trade_refund_negotiation (refundId, operator, operatorId, operation, message) VALUES (" +
            "#{refundId}, #{operator}, #{operatorId}, #{operation}, #{message})")
    int create(RefundNegotiation refundNegotiation);

    @Select("SELECT * FROM trade_refund_negotiation WHERE id = #{id}")
    RefundNegotiation getById(Long id);

    @Select("SELECT * FROM trade_refund_negotiation WHERE refundId = #{refundId} ORDER BY id DESC")
    List<RefundNegotiation> listByRefundId(Long refundId);

}
