package com.ikeyit.trade.repository;


import com.ikeyit.trade.domain.OrderItem;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrderItemRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT INTO trade_order_item (orderId, status, refundStatus, buyerId, sellerId, productId, skuId, skuCode, skuAttributes, " +
            "quantity, price, paymentAmount, title, image, logisticsCompany, trackingNumber, shipTime, finishTime) VALUES (" +
            "#{orderId}, #{status}, #{refundStatus}, #{buyerId}, #{sellerId}, #{productId}, #{skuId}, #{skuCode}, #{skuAttributes}," +
            "#{quantity}, #{price}, #{paymentAmount}, #{title}, #{image}, #{logisticsCompany}, #{trackingNumber}, #{shipTime}, #{finishTime}" +
            ")")
    int create(OrderItem orderItem);

    @Update("UPDATE trade_order_item SET refundStatus = #{orderItem.refundStatus}, status = #{orderItem.status}, " +
            "logisticsCompany = #{orderItem.logisticsCompany}, trackingNumber = #{orderItem.trackingNumber}, shipTime = #{orderItem.shipTime}, " +
            "finishTime = #{orderItem.finishTime} " +
            "WHERE id = #{orderItem.id} AND status = #{status}")
    int update(OrderItem orderItem, Integer status);

    @Update("UPDATE trade_order_item SET status = #{status} " +
            "WHERE orderId = #{orderId}")
    int updateStatusByOrderId(Long orderId, Integer status);

    @Update("UPDATE trade_order_item SET status = #{status} " +
            "WHERE id = #{id}")
    int updateStatusById(Long id, Integer status);

    @Select("SELECT * FROM trade_order_item WHERE id = #{id}")
    OrderItem getById(Long id);

    @Select("SELECT * FROM trade_order_item WHERE orderId = #{orderId}")
    List<OrderItem> getByOrderId(Long orderId);

    @Select("SELECT * FROM trade_order_item WHERE orderId = #{orderId} LIMIT 1")
    OrderItem getFirstByOrderId(Long orderId);

    @Select("SELECT COUNT(*) FROM trade_order_item WHERE orderId = #{orderId} AND status != #{status}")
    int countStatusIsNot(Long orderId, Integer status);
}
