package com.ikeyit.cms.controller;


import com.ikeyit.cms.domain.CmsPost;
import com.ikeyit.cms.dto.CreatePostParam;
import com.ikeyit.cms.dto.UpdatePostParam;
import com.ikeyit.cms.service.CmsPostService;
import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 文章REST API
 */
@RestController
public class CmsController {

    @Autowired
    CmsPostService cmsPostService;

    @GetMapping("post/{id}")
    public CmsPost getPost(@PathVariable Long id) {
        return cmsPostService.getPost(id);
    }

    @PostMapping("post")
    public int createPost(@RequestBody CreatePostParam createPostParam) {
        return cmsPostService.createPost(createPostParam);
    }

    @GetMapping("posts")
    public Page<CmsPost> getPosts(Long authorId, Integer type, Integer status, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        return cmsPostService.getPosts(authorId, type, status, new PageParam(page, pageSize));
    }

    @GetMapping("posts/user")
    public Page<CmsPost> getUserPosts(Integer type, Integer status, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        return cmsPostService.getUserPosts(type, status, new PageParam(page, pageSize));
    }

    @PutMapping("post/{id}")
    public int updatePost(@PathVariable Long id,  @RequestBody UpdatePostParam updatePostParam) {
        updatePostParam.setId(id);
        return cmsPostService.updatePost(updatePostParam);
    }

}
