package com.ikeyit.cms.repository;

import com.ikeyit.cms.domain.CmsPost;
import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface CmsPostRepository {

    @Insert("INSERT INTO cms_post (title, authorId, status, type, version, content) VALUES (#{title}, #{authorId}, #{status}, #{type}, #{version}, #{content})")
    int create(CmsPost cmsPost);

    @Select("SELECT * FROM cms_post WHERE id = #{id}")
    CmsPost getById(Long id);

    @Update({ "<script>",
            "UPDATE cms_post SET title = #{cmsPost.title}, content = #{cmsPost.content}, status=${cmsPost.status}, version = version + 1 ",
            "WHERE id = #{cmsPost.id} ",
            "<if test=\"version != null\">",
            "AND version = #{version}",
            "</if>",
            "</script>"
    })
    int update(CmsPost cmsPost, Integer version);

    @Delete("DELETE FROM cms_post WHERE id = #{id}")
    int delete(Long id);

    @Select({
            "<script>",
            "SELECT * FROM cms_post <where>",
            "authorId = #{authorId} ",
            "<if test=\"status != null\">",
            "AND type = #{type} ",
            "</if>",
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
    List<CmsPost> listAll(Long authorId, Integer type, Integer status, PageParam pageParam);


    @Select({
            "<script>",
            "SELECT COUNT(*) FROM cms_post <where>",
            "authorId = #{authorId} ",
            "<if test=\"status != null\">",
            "AND type = #{type} ",
            "</if>",
            "<if test=\"status != null\">",
            "AND status = #{status} ",
            "</if>",
            "</where>",
            "</script>"
    })
    long countAll(Long authorId, Integer type, Integer status);

    default Page<CmsPost> getAll(Long authorId, Integer type, Integer status, PageParam pageParam) {
        return new Page<>(
                listAll(authorId, type, status, pageParam),
                pageParam,
                countAll(authorId, type, status));
    }
}
