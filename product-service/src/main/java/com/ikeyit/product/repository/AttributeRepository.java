package com.ikeyit.product.repository;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.product.domain.Attribute;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface AttributeRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT INTO attribute (name, inputType, searchType, attributeType, required, position) VALUES (#{name}, #{inputType}, #{searchType}, #{attributeType}, #{required}, #{position})")
    int create(Attribute attribute);

    @Update("UPDATE attribute SET name = #{name}, inputType = #{inputType}, searchType = #{searchType}, attributeType = #{attributeType}, required = #{required}, position = #{position} WHERE id = #{id}")
    int update(Attribute attribute);

    @Select("SELECT * FROM attribute LEFT JOIN category_attribute ON attribute.id = category_attribute.attributeId " +
            "WHERE category_attribute.categoryId = #{categoryId} AND attribute.obsolete = FALSE " +
            "ORDER BY category_attribute.position")
    List<Attribute> listByCategoryId(Long categoryId);

    @Select("SELECT * FROM attribute LEFT JOIN category_attribute ON attribute.id = category_attribute.attributeId WHERE category_attribute.categoryId = #{categoryId} AND attribute.obsolete = FALSE")
    @MapKey("id")
    Map<Long, Attribute> mapByCategoryId(Long categoryId);

    @Select("SELECT * FROM attribute WHERE id = #{id}")
    Attribute getById(Long id);

    @Update("UPDATE attribute SET obsolete = TRUE WHERE id = #{id}")
    int obsolete(Long id);

    @Select({
            "<script>",
            "SELECT * FROM attribute <where>",
            "<if test=\"name != null\">",
            "AND name LIKE CONCAT('%',#{name},'%' ) ",
            "</if>",
            "AND obsolete = FALSE ",
            "</where>",
            "ORDER BY id DESC ",
            "<if test=\"pageParam != null\">",
            "LIMIT #{pageParam.pageSize} OFFSET #{pageParam.offset}",
            "</if>",
            "</script>"
    })
    List<Attribute> list(String name, PageParam pageParam);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM attribute <where>",
            "<if test=\"name != null\">",
            "AND name LIKE CONCAT('%',#{name},'%' ) ",
            "</if>",
            "AND obsolete = FALSE ",
            "</where>",
            "</script>"
    })
    long count(String name);

    default Page<Attribute> get(String name, PageParam pageParam) {
        return new Page<>(
                list(name, pageParam),
                pageParam,
                count(name));
    }
}
