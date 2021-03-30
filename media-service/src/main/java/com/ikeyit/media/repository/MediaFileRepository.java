package com.ikeyit.media.repository;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.media.domain.MediaFile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface MediaFileRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT INTO media_file (sellerId, folderId, fileType, fileName, url, extension, size, width, height) VALUES (#{sellerId}, #{folderId}, #{fileType}, #{fileName}, #{url}, #{extension}, #{size}, #{width}, #{height})")
    int create(MediaFile mediaFile);


    @Select({
            "<script>",
            "SELECT * FROM media_file <where>",
            "sellerId = #{sellerId}",
            "<if test=\"fileType != null\">",
            "AND fileType = #{fileType} ",
            "</if>",
            "<if test=\"folderId != null\">",
            "AND folderId = #{folderId} ",
            "</if>",
            "</where>",
            "ORDER BY id DESC ",
            "<if test=\"pageParam != null\">",
            "LIMIT #{pageParam.pageSize} OFFSET #{pageParam.offset}",
            "</if>",
            "</script>"
    })
    List<MediaFile> list(Long sellerId, Integer fileType, Long folderId, PageParam pageParam);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM media_file <where>",
            "sellerId = #{sellerId}",
            "<if test=\"fileType != null\">",
            "AND fileType = #{fileType} ",
            "</if>",
            "<if test=\"folderId != null\">",
            "AND folderId = #{folderId} ",
            "</if>",
            "</where>",
            "</script>"
    })
    long count(Long sellerId, Integer fileType, Long folderId);

    default Page<MediaFile> get(Long sellerId, Integer fileType, Long folderId, PageParam pageParam) {
        return new Page<>(
                list(sellerId, fileType, folderId, pageParam),
                pageParam,
                count(sellerId, fileType, folderId));
    }
}
