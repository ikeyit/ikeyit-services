package com.ikeyit.trade.repository;


import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.trade.domain.Refund;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Mapper
@Repository
public interface RefundRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT INTO trade_refund (orderId, orderItemId, buyerId, sellerId, productId, skuId, skuCode, skuAttributes, " +
            "quantity, price, paymentAmount, title, image, " +
            "status, refundType, amount, reason, memo, finishTime, logisticsCompany, trackingNumber, shipTime) VALUES (" +
            "#{orderId}, #{orderItemId}, #{buyerId}, #{sellerId}, #{productId}, #{skuId}, #{skuCode}, #{skuAttributes}, " +
            "#{quantity}, #{price}, #{paymentAmount}, #{title}, #{image}, " +
            "#{status}, #{refundType}, #{amount}, #{reason}, #{memo}, #{finishTime}, #{logisticsCompany}, #{trackingNumber}, #{shipTime})")
    int create(Refund refund);

    @Update("UPDATE trade_refund SET refundType = #{refundType}, amount=#{amount}, finishTime = #{finishTime}," +
            "reason = #{reason}, memo = #{memo}, logisticsCompany = #{logisticsCompany}, trackingNumber = #{trackingNumber}, shipTime = #{shipTime} " +
            "WHERE id = #{id}")
    int update(Refund refund);

    @Update("UPDATE trade_refund SET status = #{refund.status}, refundType = #{refund.refundType}, amount=#{refund.amount}, memo=#{refund.memo}, finishTime = #{refund.finishTime}," +
            "reason = #{refund.reason}, logisticsCompany = #{refund.logisticsCompany}, trackingNumber = #{refund.trackingNumber}, shipTime = #{refund.shipTime} " +
            "WHERE id = #{refund.id} AND status = #{oldStatus}")
    int updateStatus(Refund refund, int oldStatus);

    @Select("SELECT * FROM trade_refund WHERE id = #{id}")
    Refund getById(Long id);

    @Select("SELECT * FROM trade_refund WHERE orderItemId = #{orderItemId}")
    List<Refund> listByOrderItemId(Long orderItemId);


    @Select("SELECT * FROM trade_refund WHERE buyerId = #{buyerId} ORDER BY id DESC LIMIT #{limit} OFFSET #{offset}")
    List<Refund> listByBuyer(Long buyerId, Long offset, Integer limit);

    @Select("SELECT COUNT(*) FROM trade_refund WHERE buyerId = #{buyerId}")
    long countByBuyer(Long buyerId);

    default Page<Refund> getByBuyer(Long buyerId, PageParam pageParam) {
        return new Page<>(listByBuyer(buyerId, pageParam.getOffset(), pageParam.getPageSize()), pageParam, countByBuyer(buyerId));
    }

    @Select({"<script>",
            "SELECT * FROM trade_refund <where>",
            "sellerId = #{sellerId} ",
            "<if test=\"status != null\">",
            "AND status = #{status} ",
            "</if>",
            "<if test=\"refundType != null\">",
            "AND refundType = #{refundType} ",
            "</if>",
            "</where>",
            "ORDER BY id DESC ",
            "LIMIT #{pageParam.pageSize} OFFSET #{pageParam.offset}",
            "</script>"})
    List<Refund> listBySeller(Long sellerId,  Integer status, Integer refundType, PageParam pageParam);

    @Select({"<script>",
            "SELECT COUNT(*) FROM trade_refund <where>",
            "sellerId = #{sellerId} ",
            "<if test=\"status != null\">",
            "AND status = #{status} ",
            "</if>",
            "<if test=\"refundType != null\">",
            "AND refundType = #{refundType} ",
            "</if>",
            "</where>",
            "</script>"})
    long countBySeller(Long sellerId, Integer status, Integer refundType);

    default Page<Refund> getBySeller(Long sellerId, Integer status, Integer refundType, PageParam pageParam) {
        return new Page<>(listBySeller(sellerId, status, refundType, pageParam),
                pageParam,
                countBySeller(sellerId, status, refundType));
    }

    @Select("SELECT * FROM trade_refund WHERE orderItemId = #{orderItemId} AND status != #{status}")
    Refund getStatusIsNot(Long orderItemId, Integer status);

    @Select("SELECT COUNT(*) FROM trade_refund WHERE orderId = #{orderId} AND status != #{status}")
    int countStatusIsNotByOrderId(Long orderId, Integer status);


    @Select("SELECT COUNT(*) FROM trade_refund WHERE sellerId = #{sellerId} AND status = #{status} AND createTime BETWEEN #{startTime} AND #{endTime}")
    long countBySellerStatusDuring(Long sellerId, Integer status, LocalDateTime startTime, LocalDateTime endTime);

    @Select("SELECT COUNT(*) FROM trade_refund WHERE sellerId = #{sellerId} AND status = #{status}")
    long countBySellerStatus(Long sellerId, Integer status);
}