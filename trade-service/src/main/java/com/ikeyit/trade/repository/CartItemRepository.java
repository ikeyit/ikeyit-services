package com.ikeyit.trade.repository;

import com.ikeyit.trade.domain.CartItem;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CartItemRepository {

    @Select("SELECT * FROM cart_item WHERE id = #{id}")
    CartItem getById(Long id);

    @Select("SELECT * FROM cart_item WHERE userId = #{userId}")
    List<CartItem> getByUserId(Long userId);

    @Select("SELECT * FROM cart_item WHERE userId = #{userId} AND skuId = #{skuId}")
    CartItem getByUserIdAndSkuId(Long userId, Long skuId);

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT cart_item (userId, skuId, price, quantity) VALUES (#{userId}, #{skuId}, #{price}, #{quantity})")
    int create(CartItem cartItem);

    @Update("UPDATE cart_item SET skuId=#{skuId}, price = #{price}, quantity = #{quantity}, status = #{status} WHERE id = #{id}")
    int update(CartItem cartItem);

    @Delete("DELETE FROM cart_item WHERE id = #{id}")
    int delete(Long id);

    @Delete("DELETE FROM cart_item WHERE userId = #{userId} and skuId = #{skuId}")
    int deleteByUserIdAndSkuId(Long userId, Long skuId);
}
