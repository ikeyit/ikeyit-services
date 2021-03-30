package com.ikeyit.product.repository;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.product.domain.ShopCategoryProduct;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface ShopCategoryProductRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT INTO shop_category_product (productId, shopCategoryId1, shopCategoryId2) VALUES (#{productId}, #{shopCategoryId1}, #{shopCategoryId2})")
    int create(ShopCategoryProduct shopCategoryProduct);

    @Delete("DELETE FROM shop_category_product WHERE id = #{id}")
    int delete(Long id);

    @Delete("DELETE FROM shop_category_product WHERE shopCategoryId1 = #{shopCategoryId1}")
    int deleteByShopCategoryId1(Long shopCategoryId1);

    @Delete("DELETE FROM shop_category_product WHERE shopCategoryId2 = #{shopCategoryId2}")
    int deleteByShopCategoryId2(Long shopCategoryId2);


    @Delete("DELETE FROM shop_category_product WHERE shopCategoryId1 = #{shopCategoryId1} AND productId = #{productId}")
    int deleteByShopCategoryId1AndProductId(Long shopCategoryId1, Long productId);

    @Delete("DELETE FROM shop_category_product WHERE shopCategoryId2 = #{shopCategoryId2} AND productId = #{productId}")
    int deleteByShopCategoryId2AndProductId(Long shopCategoryId2, Long productId);

    @Select({"<script>",
            "SELECT * FROM shop_category_product WHERE shopCategoryId2 = #{shopCategoryId2}",
            "<if test=\"pageParam != null\">",
            "LIMIT #{pageParam.pageSize} OFFSET #{pageParam.offset}",
            "</if>",
            "</script>"
    })
    List<ShopCategoryProduct> listByShopCategoryId2(Long shopCategoryId2, PageParam pageParam);

    @Select("SELECT COUNT(*) FROM shop_category_product WHERE shopCategoryId2 = #{shopCategoryId2}")
    long countByShopCategoryId2(Long shopCategoryId2);


    default Page<ShopCategoryProduct>  getByShopCategoryId2(Long shopCategoryId2, PageParam pageParam) {
        return new Page<>(
                listByShopCategoryId2(shopCategoryId2,  pageParam),
                pageParam,
                countByShopCategoryId2(shopCategoryId2));
    }
}
