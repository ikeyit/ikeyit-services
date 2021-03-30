package com.ikeyit.product.repository;

import com.ikeyit.product.domain.CategoryAttribute;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface CategoryAttributeRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT IGNORE INTO category_attribute (categoryId, attributeId, position) VALUES (#{categoryId}, #{attributeId},#{position})")
    int create(CategoryAttribute categoryAttribute);

    @Update("UPDATE category_attribute SET position = #{position} WHERE categoryId = #{categoryId} AND attributeId = #{attributeId}")
    int update(CategoryAttribute categoryAttribute);

    @Select("SELECT * FROM category_attribute WHERE categoryId = #{categoryId}")
    @MapKey("attributeId")
    Map<Long, CategoryAttribute> mapByAttributeId(Long categoryId);

    @Delete("DELETE FROM category_attribute WHERE id = #{id}")
    int delete(Long id);
}
