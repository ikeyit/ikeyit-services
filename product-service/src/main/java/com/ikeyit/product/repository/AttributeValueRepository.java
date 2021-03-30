package com.ikeyit.product.repository;

import com.ikeyit.product.domain.AttributeValue;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface AttributeValueRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT INTO attribute_value (attributeId, productId, val, position, obsolete) VALUES (#{attributeId}, #{productId}, #{val}, #{position}, #{obsolete})")
    int create(AttributeValue attributeValue);

    @Update("UPDATE attribute_value SET val = #{val}, position = #{position}, obsolete = #{obsolete} WHERE id = #{id}")
    int update(AttributeValue attributeValue);

    @Update("UPDATE attribute_value SET obsolete = TRUE WHERE id = #{id}")
    int obsolete(Long id);

    @Update("UPDATE attribute_value SET obsolete = TRUE WHERE attributeId = #{attributeId}")
    int obsoleteByAttributeId(Long attributeId);

    @Update("UPDATE attribute_value SET obsolete = TRUE  WHERE id = #{id} AND productId = #{productId}")
    int obsoleteByProductId(Long id, Long productId);

    @Select("SELECT * FROM attribute_value LEFT JOIN product_attribute_value ON attribute_value.id = product_attribute_value.attributeValueId " +
            "WHERE product_attribute_value.productId = #{productId} " +
            "ORDER BY product_attribute_value.position")
    List<AttributeValue> listByProductId(Long productId);

    @Select("SELECT * FROM attribute_value LEFT JOIN product_attribute_value ON attribute_value.id = product_attribute_value.attributeValueId " +
            "WHERE product_attribute_value.productId = #{productId}")
    @MapKey("id")
    Map<Long, AttributeValue> mapByProductId(Long productId);

    @Select("SELECT * FROM attribute_value LEFT JOIN category_attribute ON attribute_value.attributeId = category_attribute.attributeId " +
            "WHERE category_attribute.categoryId = #{categoryId} AND attribute_value.productId = 0 AND attribute_value.obsolete = FALSE " +
            "ORDER BY category_attribute.position")
    List<AttributeValue> listByCategoryId(Long categoryId);

    @Select("SELECT * FROM attribute_value WHERE id = #{id}")
    AttributeValue getById(Long id);

    @Select("SELECT * FROM attribute_value WHERE attributeId = #{attributeId} AND productId = 0 AND obsolete = FALSE ORDER BY position")
    List<AttributeValue> listByAttributeId(Long attributeId);


}
