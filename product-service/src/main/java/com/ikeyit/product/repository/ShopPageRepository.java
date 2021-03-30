package com.ikeyit.product.repository;


import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.product.domain.ShopPage;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface ShopPageRepository {

    @Insert("INSERT INTO shop_page (name, sellerId, preferred, status, type, version, content) VALUES (#{name}, #{sellerId}, #{preferred}, #{status}, #{type}, #{version}, #{content})")
    int create(ShopPage shopPage);

    @Select("SELECT * FROM shop_page WHERE id = #{id}")
    ShopPage getById(Long id);

    @Update({ "<script>",
            "UPDATE shop_page SET name = #{shopPage.name}, preferred = #{shopPage.preferred}, content = #{shopPage.content}, status=${shopPage.status}, version = version + 1 ",
            "WHERE id = #{shopPage.id} ",
            "<if test=\"version != null\">",
            "AND version = #{version}",
            "</if>",
            "</script>"
    })
    int update(ShopPage shopPage, Integer version);

    @Delete("DELETE FROM shop_page WHERE id = #{id}")
    int delete(Long id);


    @Update("UPDATE shop_page SET preferred = FALSE WHERE sellerId = #{sellerId} AND preferred = TRUE")
    int clearPreferred(Long sellerId);

    @Select("SELECT * FROM shop_page WHERE sellerId = #{sellerId} AND type = #{type} AND preferred = TRUE ORDER BY id LIMIT 1")
    ShopPage getPreferred(Long sellerId, Integer type);

    @Select({
            "<script>",
            "SELECT * FROM shop_page <where>",
            "sellerId = #{sellerId} ",
            "AND type = #{type} ",
            "<if test=\"status != null\">",
            "AND status = #{status} ",
            "</if>",
            "</where>",
            "ORDER BY ",
            "id DESC ",
            "<if test=\"pageParam != null\">",
            "LIMIT #{pageParam.pageSize} OFFSET #{pageParam.offset}",
            "</if>",
            "</script>"
    })
    List<ShopPage> listAll(Long sellerId, Integer type, Integer status, PageParam pageParam);


    @Select({
            "<script>",
            "SELECT COUNT(*) FROM shop_page <where>",
            "sellerId = #{sellerId} ",
            "AND type = #{type} ",
            "<if test=\"status != null\">",
            "AND status = #{status} ",
            "</if>",
            "</where>",
            "</script>"
    })
    long countAll(Long sellerId, Integer type, Integer status);

    default Page<ShopPage> getAll(Long sellerId, Integer type, Integer status, PageParam pageParam) {
        return new Page<>(
                listAll(sellerId, type, status, pageParam),
                pageParam,
                countAll(sellerId, type, status));
    }
}
