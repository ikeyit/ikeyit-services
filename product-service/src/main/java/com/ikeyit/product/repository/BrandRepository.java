package com.ikeyit.product.repository;

import com.ikeyit.product.domain.Brand;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface BrandRepository {
    @Insert("INSERT INTO brand (name, description, logo) VALUES (#{name}, #{description}, #{logo})")
    int create(Brand brand);

    @Update("UPDATE brand SET name = #{name}, description = #{description}, logo = #{logo} WHERE id = #{id}")
    int update(Brand brand);

    @Select("SELECT * FROM brand WHERE id = #{id}")
    Brand getById(Long id);

    @Delete("DELETE FROM brand WHERE id = #{id}")
    int delete(Long id);
}
