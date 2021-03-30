package com.ikeyit.product.repository;

import com.ikeyit.product.domain.ShopCategory;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface ShopCategoryRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT INTO shop_category (sellerId, parentId, level, name, image, position) VALUES (#{sellerId}, #{parentId}, #{level}, #{name}, #{image}, #{position})")
    int create(ShopCategory shopCategory);

    @Update("UPDATE shop_category SET name = #{name}, image = #{image}, position = #{position} WHERE id = #{id}")
    int update(ShopCategory shopCategory);

    @Delete("DELETE FROM shop_category WHERE id = #{id}")
    int delete(Long id);

    @Delete("DELETE FROM shop_category WHERE parentId = #{parentId}")
    int deleteByParentId(Long parentId);

    @Select("SELECT * FROM shop_category WHERE id = #{id}")
    ShopCategory getById(Long id);

    @Select("SELECT * FROM shop_category WHERE parentId = #{parentId} ORDER BY position")
    List<ShopCategory> listByParentId(Long parentId) ;

    @Select("SELECT * FROM shop_category WHERE sellerId = #{sellerId} ORDER BY level, position")
    List<ShopCategory> listBySellerId(Long sellerId);
}
