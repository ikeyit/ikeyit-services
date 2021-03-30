package com.ikeyit.product.repository;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.product.domain.Product;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface ProductRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT INTO product (sellerId, categoryId, brandId, title, subtitle, model, image, images, video, detail, status, sales, recommendation, price, promotionPrice)" +
            " VALUES (#{sellerId}, #{categoryId}, #{brandId}, #{title}, #{subtitle}, #{model}, #{image}, #{images}, #{video}, #{detail}, #{status}, #{sales}, #{recommendation}, #{price}, #{promotionPrice})")
    int create(Product product);

    @Update("UPDATE product SET brandId = #{brandId},title=#{title},subtitle=#{subtitle},model=#{model},image=#{image},images=#{images},video=#{video},detail=#{detail},status=#{status}," +
            "sales = #{sales}, recommendation = #{recommendation}, price=#{price}, promotionPrice=#{promotionPrice} WHERE id = #{id}")
    int update(Product product);

    @Update("UPDATE product SET sales = sales + #{mount} WHERE id = #{id}")
    int increaseSales(Long id, int mount);

    @Update("UPDATE product SET recommendation = #{recommendation} WHERE id = #{id}")
    int updateRecommendation(Long id, Long recommendation);

    @Update("UPDATE product SET status = #{newStatus} WHERE id = #{id} and status = #{oldStatus}")
    int updateStatus(Long id, Integer oldStatus, Integer newStatus);

    @Select("SELECT * FROM product WHERE id = #{id}")
    Product getById(Long id);


    @Select({
            "<script>",
            "SELECT * FROM product <where>",
            "<if test=\"id != null\">",
            "AND id = #{id} ",
            "</if>",
            "<if test=\"sellerId != null\">",
            "AND sellerId = #{sellerId} ",
            "</if>",
            "<if test=\"status != null\">",
            "AND status = #{status} ",
            "</if>",
            "<if test=\"model != null and model != ''\">",
            "AND model = #{model} ",
            "</if>",
            "<if test=\"title != null and title != ''\">",
            "AND title LIKE CONCAT('%',#{title},'%' ) ",
            "</if>",
            "</where>",
            "ORDER BY ",
            "<choose>",
            "<when test=\"sortCriteria == 'price_desc'\">",
            "price DESC ",
            "</when>",
            "<when test=\"sortCriteria == 'price_asc'\">",
            "price ASC ",
            "</when>",
            "<when test=\"sortCriteria == 'sales_desc'\">",
            "sales DESC ",
            "</when>",
            "<when test=\"sortCriteria == 'sales_asc'\">",
            "sales ASC ",
            "</when>",
            "<when test=\"sortCriteria == 'stock_desc'\">",
            "sales DESC ",
            "</when>",
            "<when test=\"sortCriteria == 'stock_asc'\">",
            "sales ASC ",
            "</when>",
            "<when test=\"sortCriteria == 'createTime_asc'\">",
            "id ASC ",
            "</when>",
            "<otherwise>",
            "id DESC ",
            " </otherwise>",
            "</choose>",
            "<if test=\"pageParam != null\">",
            "LIMIT #{pageParam.pageSize} OFFSET #{pageParam.offset}",
            "</if>",
            "</script>"
    })
    List<Product> listBySellerId(Long sellerId, Integer status, Long id, String title, String model, String sortCriteria, PageParam pageParam);


    @Select({
            "<script>",
            "SELECT COUNT(*) FROM product <where>",
            "<if test=\"id != null\">",
            "AND id = #{id} ",
            "</if>",
            "<if test=\"sellerId != null\">",
            "AND sellerId = #{sellerId} ",
            "</if>",
            "<if test=\"status != null\">",
            "AND status = #{status} ",
            "</if>",
            "<if test=\"model != null and model != ''\">",
            "AND model = #{model} ",
            "</if>",
            "<if test=\"title != null and title != ''\">",
            "AND title LIKE CONCAT('%',#{title},'%' ) ",
            "</if>",
            "</where>",
            "</script>"
    })
    long countBySellerId(Long sellerId, Integer status, Long id, String title, String model);


    default Page<Product> getBySellerId(Long sellerId, Integer status, Long id, String title, String model, String sortCriteria, PageParam pageParam) {
        return new Page<>(
                listBySellerId(sellerId, status, id, title, model, sortCriteria,  pageParam),
                pageParam,
                countBySellerId(sellerId, status, id, title, model));
    }

}
