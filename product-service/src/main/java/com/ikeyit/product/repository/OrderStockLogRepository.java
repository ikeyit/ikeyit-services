package com.ikeyit.product.repository;

import com.ikeyit.product.domain.OrderStockLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OrderStockLogRepository {

    @Select("SELECT * FROM order_stock_Log WHERE orderId = #{orderId}")
    OrderStockLog getByOrderId(Long orderId);

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT INTO order_stock_Log (orderId, content, status) VALUES (#{orderId}, #{content}, #{status})")
    void create(OrderStockLog orderStockLog);

    @Update("UPDATE order_stock_Log SET status = #{newStatus} WHERE orderId = #{orderId} AND status = #{oldStatus}")
    int updateStatus(Long orderId, Integer oldStatus, Integer newStatus);

    @Delete("DELETE FROM order_stock_Log WHERE orderId = #{orderId}")
    int delete(Long orderId);
}
