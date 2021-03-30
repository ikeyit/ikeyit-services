package com.ikeyit.cms.service;

import com.ikeyit.cms.domain.CmsPost;
import com.ikeyit.cms.dto.CreatePostParam;
import com.ikeyit.cms.dto.UpdatePostParam;
import com.ikeyit.cms.repository.CmsPostRepository;
import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.passport.resource.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;


/**
 *
 */
//TODO DTO封装
@Service
public class CmsPostService {
    private static Logger log = LoggerFactory.getLogger(CmsPostService.class);

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    CmsPostRepository cmsPostRepository;

    /**
     * 创建文章
     * @param createPostParam
     * @return
     */
    public int createPost(CreatePostParam createPostParam) {
        Long userId = authenticationService.getCurrentUserId();
        if (createPostParam.getType() == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
        CmsPost post = new CmsPost();
        post.setAuthorId(userId);
        post.setTitle(createPostParam.getTitle());
        post.setStatus(CmsPost.STATUS_DRAFT);
        post.setType(createPostParam.getType());
        post.setVersion(0);
        return cmsPostRepository.create(post);
    }

    /**
     * 通过ID获取文章所有详情
     * @param id
     * @return
     */
    @Cacheable("CmsPost")
    public CmsPost getPost(Long id) {
        log.info("读数据库");
        return cmsPostRepository.getById(id);
    }

    /**
     * 获取当前用户发布的文章
     * @param type
     * @param status
     * @param pageParam
     * @return
     */
    public Page<CmsPost> getUserPosts(Integer type, Integer status, PageParam pageParam) {
        Long userId = authenticationService.getCurrentUserId();
        return getPosts(userId, type, status, pageParam);
    }


    /**
     * 查询文章
     * @param authorId 作者， 不能为NULL
     * @param type 类型, 为NULL，则查询所有类型
     * @param status 状态，如果为NULL，则查询所有状态
     * @param pageParam 分页参数
     * @return
     */
    @Cacheable("CmsPosts")
    public Page<CmsPost> getPosts(Long authorId, Integer type, Integer status, PageParam pageParam) {
        return cmsPostRepository.getAll(authorId, type, status, pageParam);
    }


    /**
     * 更新文章
     * @param updatePostParam
     * @return
     */
    @Caching(evict= {
            @CacheEvict(value = "CmsPost", key= "#updatePostParam.id"),
            @CacheEvict(value = "CmsPosts", allEntries = true)} //清除所有列表
    )
    public int updatePost(UpdatePostParam updatePostParam) {
        Long userId = authenticationService.getCurrentUserId();
        if (updatePostParam.getId() == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
        CmsPost cmsPost = cmsPostRepository.getById(updatePostParam.getId());
        if (cmsPost == null)
            throw new BusinessException(CommonErrorCode.NOT_FOUND);
        if (!userId.equals(cmsPost.getAuthorId()))
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        cmsPost.setContent(updatePostParam.getContent());
        if (updatePostParam.getTitle() != null)
            cmsPost.setTitle(updatePostParam.getTitle());
        if (updatePostParam.getStatus() != null)
            cmsPost.setStatus(updatePostParam.getStatus());
        int ret = cmsPostRepository.update(cmsPost, updatePostParam.getVersion());
        if (ret <= 0)
            throw new BusinessException(CommonErrorCode.RESOURCE_UPDATE_CONFLICT);

        return ret;
    }
}
