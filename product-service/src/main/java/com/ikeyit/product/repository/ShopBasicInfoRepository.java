package com.ikeyit.product.repository;

import com.ikeyit.product.domain.ShopBasicInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface ShopBasicInfoRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT INTO shop_basic_info (sellerId, name, avatar, description) VALUES (#{sellerId}, #{name}, #{avatar}, #{description})")
    int create(ShopBasicInfo shopBasicInfo);

    @Select("SELECT * FROM shop_basic_info WHERE id = #{id}")
    ShopBasicInfo getById(Long id);

    @Select("SELECT * FROM shop_basic_info WHERE sellerId = #{sellerId}")
    ShopBasicInfo getBySellerId(Long sellerId);

    @Update("UPDATE shop_basic_info SET name = #{name}, avatar = #{avatar}, description = #{description} WHERE id = #{id}")
    int update(ShopBasicInfo shopBasicInfo);
}
