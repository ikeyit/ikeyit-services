package com.ikeyit.product.repository;

import com.ikeyit.product.domain.ProductAttributeValue;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface ProductAttributeValueRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT IGNORE INTO product_attribute_value (productId, attributeValueId, position) VALUES (#{productId}, #{attributeValueId},#{position})")
    int create(ProductAttributeValue productAttributeValue);

    @Update("UPDATE product_attribute_value SET position = #{position} WHERE productId = #{productId} AND attributeValueId = #{attributeValueId}")
    int update(ProductAttributeValue productAttributeValue);


    @Select("SELECT * FROM product_attribute_value WHERE productId = #{productId}")
    @MapKey("attributeValueId")
    Map<Long, ProductAttributeValue> mapByProductId(Long productId);

    @Delete("DELETE FROM product_attribute_value WHERE productId = #{productId} AND attributeValueId = #{attributeValueId}")
    int delete(Long productId, Long attributeValueId);

    @Delete("DELETE FROM product_attribute_value WHERE id = #{id}")
    int deleteById(Long id);
}
