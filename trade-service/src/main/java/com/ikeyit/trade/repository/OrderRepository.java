package com.ikeyit.trade.repository;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.trade.domain.Order;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
@Repository
public interface OrderRepository {

    @Select("SELECT * FROM trade_order WHERE id = #{id}")
    Order getById(Long id);


    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT trade_order (status, closeReason, " +
            "buyerId, buyerName, buyerMemo, sellerId, sellerMemo, " +
            "orderType, goodsQuantity, goodsAmount, paymentAmount, discountAmount, freightAmount, " +
            "receiverName, receiverPhone, receiverProvince, receiverCity, receiverDistrict, receiverStreet, " +
            "invoiceTitle, invoiceContent, invoicePayerTaxId, " +
            "payWay, payOrderId, payTime, expireTime, source, finishTime, logisticsCompany, trackingNumber, shipTime" +
            ") VALUES (#{status}, #{closeReason}," +
            "#{buyerId}, #{buyerName}, #{buyerMemo}, #{sellerId}, #{sellerMemo}, " +
            "#{orderType}, #{goodsQuantity}, #{goodsAmount}, #{paymentAmount}, #{discountAmount}, #{freightAmount}, " +
            "#{receiverName}, #{receiverPhone}, #{receiverProvince}, #{receiverCity}, #{receiverDistrict}, #{receiverStreet}, " +
            "#{invoiceTitle}, #{invoiceContent}, #{invoicePayerTaxId}, " +
            "#{payWay}, #{payOrderId}, #{payTime}, #{expireTime}, #{source}, #{finishTime}, #{logisticsCompany}, #{trackingNumber}, #{shipTime})")
    int create(Order order);



    @Select({"<script>",
            "SELECT * FROM trade_order <where>",
            "<if test=\"id != null\">",
            "AND id = #{id} ",
            "</if>",
            "<if test=\"trackingNumber != null and trackingNumber != ''\">",
            "AND trackingNumber = #{trackingNumber} ",
            "</if>",
            "<if test=\"buyerId != null\">",
            "AND buyerId = #{buyerId} ",
            "</if>",
            "<if test=\"receiverPhone != null and receiverPhone != ''\">",
            "AND receiverPhone = #{receiverPhone} ",
            "</if>",
            "<if test=\"receiverName != null and receiverName != ''\">",
            "AND receiverName = #{receiverName} ",
            "</if>",
            "<if test=\"sellerId != null\">",
            "AND sellerId = #{sellerId} ",
            "</if>",
            "<if test=\"status != null\">",
            "AND status = #{status} ",
            "</if>",
            "<if test=\"createTimeStart != null\">",
            "AND createTime &gt;= #{createTimeStart} ",
            "</if>",
            "<if test=\"createTimeEnd != null\">",
            "AND createTime &lt;= #{createTimeEnd} ",
            "</if>",
            "</where>",
            "ORDER BY ",
            "<choose>",
            "<when test=\"sortCriteria == 'createTime_asc'\">",
            "id ASC ",
            "</when>",
            "<otherwise>",
            "id DESC ",
            " </otherwise>",
            "</choose>",
            "LIMIT #{pageParam.pageSize} OFFSET #{pageParam.offset}",
            "</script>"})
    List<Order> list(Long id, Long buyerId, Long sellerId, Integer status,
                     String receiverName, String receiverPhone, String trackingNumber,
                     LocalDateTime createTimeStart, LocalDateTime createTimeEnd,
                     String sortCriteria, PageParam pageParam);


    @Select({"<script>",
            "SELECT COUNT(*) FROM trade_order <where>",
            "<if test=\"id != null\">",
            "AND id = #{id} ",
            "</if>",
            "<if test=\"trackingNumber != null and trackingNumber != ''\">",
            "AND trackingNumber = #{trackingNumber} ",
            "</if>",
            "<if test=\"buyerId != null\">",
            "AND buyerId = #{buyerId} ",
            "</if>",
            "<if test=\"receiverPhone != null and receiverPhone != ''\">",
            "AND receiverPhone = #{receiverPhone} ",
            "</if>",
            "<if test=\"receiverName != null and receiverName != ''\">",
            "AND receiverName = #{receiverName} ",
            "</if>",
            "<if test=\"sellerId != null\">",
            "AND sellerId = #{sellerId} ",
            "</if>",
            "<if test=\"status != null\">",
            "AND status = #{status} ",
            "</if>",
            "<if test=\"createTimeStart != null\">",
            "AND createTime &gt;= #{createTimeStart} ",
            "</if>",
            "<if test=\"createTimeEnd != null\">",
            "AND createTime &lt;= #{createTimeEnd} ",
            "</if>",
            "</where>",
            "</script>"})
    long count(Long id, Long buyerId, Long sellerId, Integer status,
               String receiverName, String receiverPhone, String trackingNumber,
               LocalDateTime createTimeStart, LocalDateTime createTimeEnd);

    default Page<Order> get(Long id, Long buyerId, Long sellerId, Integer status,
                            String receiverName, String receiverPhone, String trackingNumber,
                            LocalDateTime createTimeStart,LocalDateTime createTimeEnd,
                            String direction, PageParam pageParam) {
        return new Page<>(
                list(id, buyerId, sellerId, status,
                        receiverName, receiverPhone, trackingNumber,
                        createTimeStart, createTimeEnd,
                        direction, pageParam),
                pageParam,
                count(id, buyerId, sellerId, status,
                        receiverName, receiverPhone, trackingNumber,
                        createTimeStart, createTimeEnd));
    }



//    @Update("UPDATE trade_order SET status = #{newStatus} WHERE id = #{id} AND status = #{oldStatus}")
//    int updateStatus(Long id, Integer oldStatus, Integer newStatus);


    @Update("UPDATE trade_order SET payWay = #{order.payWay}, payOrderId = #{order.payOrderId}, " +
            "finishTime = #{order.finishTime}, payTime = #{order.payTime}, " +
            "logisticsCompany = #{order.logisticsCompany}, trackingNumber = #{order.trackingNumber}, shipTime = #{order.shipTime}, " +
            "closeReason = #{order.closeReason}, status = #{order.status} " +
            "WHERE id = #{order.id} AND status = #{oldStatus}")
    int updateStatus(Order order, Integer oldStatus);



    @Update("UPDATE trade_order SET sellerMemo=#{sellerMemo} " +
            "WHERE id = #{id}")
    int updateSellerMemo(Order order);

    @Select("SELECT COUNT(*) FROM trade_order WHERE sellerId = #{sellerId} AND status = #{status} AND createTime BETWEEN #{startTime} AND #{endTime}")
    long countBySellerStatusDuring(Long sellerId, Integer status, LocalDateTime startTime, LocalDateTime endTime);

    @Select("SELECT COUNT(*) FROM trade_order WHERE sellerId = #{sellerId} AND status = #{status}")
    long countBySellerStatus(Long sellerId, Integer status);

    @Select("SELECT COUNT(*) FROM trade_order WHERE sellerId = #{sellerId} AND createTime BETWEEN #{startTime} AND #{endTime}")
    long countBySellerDuring(Long sellerId, LocalDateTime startTime, LocalDateTime endTime);

    @Select("SELECT COALESCE(SUM(paymentAmount),0) FROM trade_order WHERE sellerId = #{sellerId} AND createTime BETWEEN #{startTime} AND #{endTime}")
    BigDecimal sumPaymentAmountBySeller(Long sellerId, LocalDateTime startTime, LocalDateTime endTime);
}
