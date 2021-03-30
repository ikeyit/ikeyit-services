package com.ikeyit.product.repository;

import com.ikeyit.product.domain.Sku;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SkuRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT INTO sku (sellerId, productId, status, code, image, price, promotionPrice, stock, lockedStock, sales, attributes) VALUES (#{sellerId}, #{productId}, #{status}, #{code}, #{image}, #{price}, #{promotionPrice}, #{stock}, #{lockedStock}, #{sales}, #{attributes})")
    int create(Sku sku);

    @Insert("UPDATE sku SET status = #{status}, code = #{code}, image = #{image}, price = #{price}, promotionPrice = #{promotionPrice}, stock = #{stock}, lockedStock = #{lockedStock}, sales = #{sales}, attributes = #{attributes} WHERE id = #{id}")
    int update(Sku sku);

    @Update("UPDATE sku SET status = #{status} WHERE id = #{id}")
    int updateStatus(Long id, Integer status);

    @Update("UPDATE sku SET stock = stock - #{amount} WHERE id = #{id} AND stock - #{amount} >= 0")
    int reduceStock(Long id, Long amount);

    @Update("UPDATE sku SET stock = stock + #{amount} WHERE id = #{id}")
    int addStock(Long id, Long amount);

    @Update("UPDATE sku SET stock = #{stock} WHERE id = #{id}")
    int updateStock(Long id, Long stock);

    @Select("SELECT * FROM sku WHERE id = #{id}")
    Sku getById(Long id);

    @Select("SELECT * FROM sku WHERE productId = #{productId} AND status != 4")
    List<Sku> listSkusByProductId(Long productId);

    @Select("SELECT * FROM sku WHERE productId = #{productId} AND status != 4")
    @MapKey("attributes")
    Map<String, Sku> mapSkusByProductId(Long productId);

    @Select("SELECT SUM(stock) FROM sku WHERE productId = #{productId} AND status = 0")
    Long sumProductStock(Long productId);

}
