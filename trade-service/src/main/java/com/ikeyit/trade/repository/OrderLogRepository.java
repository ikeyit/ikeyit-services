package com.ikeyit.trade.repository;

import com.ikeyit.trade.domain.OrderLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrderLogRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT INTO trade_order_log (orderId, orderStatus, operator, message) VALUES (" +
            "#{orderId}, #{orderStatus}, #{operator}, #{message})")
    int create(OrderLog orderLog);

    @Select("SELECT * FROM trade_order_log WHERE id = #{id}")
    OrderLog getById(Long id);

    @Select("SELECT * FROM trade_order_log WHERE orderId = #{orderId}")
    List<OrderLog> getByOrderId(Long orderId);

}
