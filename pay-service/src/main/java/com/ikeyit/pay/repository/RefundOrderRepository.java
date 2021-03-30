package com.ikeyit.pay.repository;

import com.ikeyit.pay.domain.RefundOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RefundOrderRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT pay_refund_order (payOrderId, refundType, refundId, status, " +
            "refundNo, tradeNo, reason, payWay, refundAmount, paymentAmount, refundData, " +
            "successTime " +
            ") VALUES (#{payOrderId},  #{refundType}, #{refundId}, #{status}," +
            "#{refundNo}, #{tradeNo}, #{reason}, #{payWay}, #{refundAmount}, #{paymentAmount}, #{refundData}, " +
            "#{successTime})")
    int create(RefundOrder refundOrder);

    @Select("SELECT * FROM pay_refund_order WHERE refundId = #{refundId} AND refundType = #{refundType}")
    RefundOrder getByRefundId(String refundType, Long refundId);

    @Select("SELECT * FROM pay_refund_order WHERE refundNo = #{refundNo}")
    RefundOrder getByRefundNo(String refundNo);

    @Update("UPDATE pay_refund_order SET status = #{refundOrder.status}, " +
            "refundData = #{refundOrder.refundData}, " +
            "successTime = #{refundOrder.successTime} " +
            "WHERE id = #{refundOrder.id} AND status = #{status}")
    int update(RefundOrder refundOrder, Integer status);

}
