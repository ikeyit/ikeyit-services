package com.ikeyit.product.repository;

import com.ikeyit.product.domain.Category;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface CategoryRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT INTO category (parentId, status, level, name, description) VALUES (#{parentId}, #{status}, #{level}, #{name}, #{description})")
    int create(Category category);

    @Update("UPDATE category SET status = #{status}, name = #{name}, description = #{description} WHERE id = #{id}")
    int update(Category category);

    @Update("UPDATE category SET obsolete = TRUE WHERE id = #{id}")
    int obsolete(Long id);

    @Update("UPDATE category SET obsolete = TRUE WHERE parentId = #{parentId}")
    int obsoleteByParentId(Long parentId);

    @Update("UPDATE category child LEFT JOIN category parent ON child.parentId = parent.id SET child.obsolete = TRUE WHERE parent.parentId = #{grandparentId}")
    int obsoleteByGrandparentId(Long grandparentId);

    @Select("SELECT * FROM category WHERE id = #{id}")
    Category getCategoryById(Long id);

    @Select("SELECT * FROM category WHERE parentId = #{parentId} WHERE obsolete = FALSE")
    List<Category> listCategoriesByParentId(Long parentId) ;

    @Select("SELECT * FROM category WHERE obsolete = FALSE ORDER BY level ")
    List<Category> listAllCategories();
}
