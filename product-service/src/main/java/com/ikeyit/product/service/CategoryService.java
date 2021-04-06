package com.ikeyit.product.service;

import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.product.domain.Category;
import com.ikeyit.product.domain.CategoryAttribute;
import com.ikeyit.product.dto.AttributeDTO;
import com.ikeyit.product.dto.CategoryDTO;
import com.ikeyit.product.dto.EditCategoryParam;
import com.ikeyit.product.dto.UpdateCategoryAttributesParam;
import com.ikeyit.product.repository.CategoryAttributeRepository;
import com.ikeyit.product.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    static Logger log = LoggerFactory.getLogger(CategoryService.class);
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    AttributeService attributeService;

    @Autowired
    CategoryAttributeRepository categoryAttributeRepository;

    /**
     * 创建类目
     * 需要平台管理权限
     * @param editCategoryParam
     * @return
     */
    public int createCategory(EditCategoryParam editCategoryParam) {
        authenticationService.requireAuthority("r_super");
        Category category = new Category();
        Long parentId = editCategoryParam.getParentId();
        if (parentId != null && parentId > 0) {
            //创建子类目
            Category parentCategory = getExistingCategory(parentId);
            if (parentCategory.getLevel() > 1)
                throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "只允许创建3级");
            category.setLevel(parentCategory.getLevel() + 1);
            category.setParentId(parentId);
        } else {
            //创建根类目
            category.setLevel(0);
            category.setParentId(0L);
        }
        category.setName(editCategoryParam.getName());
        category.setDescription(editCategoryParam.getDescription());
        category.setStatus(Category.STATUS_VISIBLE);
        return categoryRepository.create(category);
    }

    /**
     * 更新类目基本信息
     * 需要平台管理权限
     * @param editCategoryParam
     * @return
     */
    public int updateCategory(EditCategoryParam editCategoryParam) {
        authenticationService.requireAuthority("r_super");
        Category category = getExistingCategory(editCategoryParam.getId());
        if (editCategoryParam.getStatus()!=null)
            category.setStatus(editCategoryParam.getStatus());
        category.setName(editCategoryParam.getName());
        category.setDescription(editCategoryParam.getDescription());
        return categoryRepository.update(category);
    }

    /**
     * 更新类目关联的属性
     * 需要平台管理权限
     * @param updateCategoryAttributesParam
     */
    @Transactional
    public void updateCategoryAttributes(UpdateCategoryAttributesParam updateCategoryAttributesParam) {
        authenticationService.requireAuthority("r_super");
        Long categoryId = updateCategoryAttributesParam.getId();
        Category category = getExistingCategory(categoryId);
        Map<Long, CategoryAttribute> prevCategoryAttributes = categoryAttributeRepository.mapByAttributeId(categoryId);

        List<UpdateCategoryAttributesParam.AttributeParam> attributeParams = updateCategoryAttributesParam.getAttributes();
        if (attributeParams != null) {
            int i = 0;
            for (UpdateCategoryAttributesParam.AttributeParam attributeParam : attributeParams) {
                Long attributeId = attributeParam.getAttributeId();
                if (attributeId == null)
                    throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "属性ID不能为空");
                CategoryAttribute categoryAttribute = prevCategoryAttributes.get(attributeId);
                if (categoryAttribute == null) {
                    //新建
                    categoryAttribute = new CategoryAttribute();
                    categoryAttribute.setCategoryId(categoryId);
                    categoryAttribute.setAttributeId(attributeId);
                    categoryAttribute.setPosition(i);
                    categoryAttributeRepository.create(categoryAttribute);
                } else {
                    //更新
                    categoryAttribute.setPosition(i);
                    categoryAttributeRepository.update(categoryAttribute);
                    prevCategoryAttributes.remove(attributeId);
                }
                i++;
            }
        }

        //删除
        prevCategoryAttributes.values().forEach(categoryAttribute -> {
            categoryAttributeRepository.delete(categoryAttribute.getId());
        });
    }

    /**
     * 逻辑删除类目
     * 需要平台管理权限
     * @param id
     * @return
     */
    public int obsoleteCategory(Long id) {
        authenticationService.requireAuthority("r_super");
        Category category = getExistingCategory(id);
        if (category.getLevel() == 0) {
            categoryRepository.obsoleteByGrandparentId(id);
        }
        if (category.getLevel() <= 1) {
            categoryRepository.obsoleteByParentId(id);
        }

        return categoryRepository.obsolete(id);
    }

    /**
     *
     * @param id
     * @return
     */
    public CategoryDTO getCategoryDetail(Long id) {
        authenticationService.requireAuthority("r_super");
        Category category = getExistingCategory(id);
        CategoryDTO categoryDTO = buildCategoryDTO(category);
        List<AttributeDTO>  attributes = attributeService.getAttributesByCategory(id);
        categoryDTO.setAttributes(attributes);
        return categoryDTO;
    }

    private Category getExistingCategory(Long id) {
        if (id == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "类目ID不能为空");
        Category category = categoryRepository.getCategoryById(id);
        if (category == null || Boolean.TRUE.equals(category.getObsolete()))
            throw new BusinessException(CommonErrorCode.NOT_FOUND, "类目"+ id);
        return category;
    }

    private CategoryDTO buildCategoryDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setLevel(category.getLevel());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());
        categoryDTO.setParentId(category.getParentId());
        return categoryDTO;
    }

    public List<CategoryDTO> getCategories(Long parentId) {
        List<Category> categories = categoryRepository.listCategoriesByParentId(parentId);
        return categories.stream().map(category -> buildCategoryDTO(category)).collect(Collectors.toList());
    }

    public CategoryDTO getCategory(Long categoryId) {
        if (categoryId == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "类目ID不能为空");
        Category category = categoryRepository.getCategoryById(categoryId);
        if (category == null)
            throw new BusinessException(CommonErrorCode.NOT_FOUND, categoryId);
        return buildCategoryDTO(category);
    }


    public List<CategoryDTO> getCategoryPath(Long categoryId) {
        LinkedList<CategoryDTO> path = new LinkedList<>();
        while(categoryId != null && categoryId > 0) {
            CategoryDTO categoryDTO = getCategory(categoryId);
            categoryId = categoryDTO.getParentId();
            path.addFirst(categoryDTO);
        }
        return path;
    }

    public List<CategoryDTO> getAllCategories() {
        //DAO必须返回按照level排好序的列表
        List<Category> categories = categoryRepository.listAllCategories();
        List<CategoryDTO> rootCategoryDTOs = new ArrayList<>(16);
        HashMap<Long, CategoryDTO> map = new HashMap<>();

        for (Category category : categories) {
            CategoryDTO categoryDTO = buildCategoryDTO(category);
            map.put(category.getId(), categoryDTO);

            if (category.getParentId() == null || category.getParentId() == 0L) {
                rootCategoryDTOs.add(categoryDTO);
            } else {
                CategoryDTO parent = map.get(category.getParentId());
                if (parent == null) {
                    //数据不一致啦
                    log.warn("Category的父节点不存在！ID：" + category.getId());
                } else {
                    List<CategoryDTO> children = parent.getChildren();
                    if (children == null) {
                        children = new ArrayList<>();
                        parent.setChildren(children);
                    }

                    children.add(categoryDTO);
                }
            }
        }

        return rootCategoryDTOs;
    }


}
