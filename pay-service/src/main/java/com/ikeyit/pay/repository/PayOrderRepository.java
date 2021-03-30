package com.ikeyit.pay.repository;

import com.ikeyit.pay.domain.PayOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Mapper
@Repository
public interface PayOrderRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT pay_order (buyerId, orderType, orderId, status, " +
            "tradeNo, payWay, paymentAmount, refundAmount, subject, transactionId, " +
            "transactionData, clientIp, expireTime, successTime" +
            ") VALUES (#{buyerId}, #{orderType}, #{orderId}, #{status}, " +
            "#{tradeNo}, #{payWay}, #{paymentAmount}, #{refundAmount}, #{subject}, #{transactionId}, " +
            "#{transactionData}, #{clientIp}, #{expireTime}, #{successTime})")
    int create(PayOrder payOrder);

    @Select("SELECT * FROM pay_order WHERE orderId = #{orderId} AND orderType = #{orderType}")
    PayOrder getByOrder(String orderType, Long orderId);

    @Select("SELECT * FROM pay_order WHERE id = #{id}")
    PayOrder getById(Long id);

    @Select("SELECT * FROM pay_order WHERE tradeNo = #{tradeNo}")
    PayOrder getByTradeNo(String tradeNo);


    @Update("UPDATE pay_order SET status = #{payOrder.status}, payWay = #{payOrder.payWay}, " +
            "transactionId = #{payOrder.transactionId}, transactionData = #{payOrder.transactionData}, " +
            "successTime = #{payOrder.successTime}, " +
            "clientIp = #{payOrder.clientIp}, " +
            "refundAmount = #{payOrder.refundAmount} " +
            "WHERE orderId = #{payOrder.orderId} AND status = #{status}")
    int update(PayOrder payOrder, Integer status);

    @Update("UPDATE pay_order SET refundAmount = refundAmount + #{amount} " +
            "WHERE id = #{id} AND paymentAmount >= refundAmount + #{amount}")
    int addRefundAmount(Long id, BigDecimal amount);

}
