package com.ikeyit.product.controller;

import com.ikeyit.product.dto.CategoryDTO;
import com.ikeyit.product.dto.EditCategoryParam;
import com.ikeyit.product.dto.UpdateCategoryAttributesParam;
import com.ikeyit.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/category/{id}")
    public CategoryDTO getCategory(@PathVariable Long id) {
        return categoryService.getCategory(id);
    }

    @GetMapping("/category/{id}/path")
    public List<CategoryDTO> getCategoryPath(@PathVariable Long id) {
        return categoryService.getCategoryPath(id);
    }

    @GetMapping("/categories")
    public List<CategoryDTO> getCategories(@RequestParam(required = false) Long parentId) {
        return categoryService.getCategories(parentId);
    }

    @GetMapping("/categories/all")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }


    @PostMapping("/category")
    public int createCategory(@RequestBody EditCategoryParam editCategoryParam) {
        return categoryService.createCategory(editCategoryParam);
    }

    @PutMapping("/category/{id}")
    public int updateCategory(@PathVariable Long id, @RequestBody EditCategoryParam editCategoryParam) {
        editCategoryParam.setId(id);
        return categoryService.updateCategory(editCategoryParam);
    }

    @DeleteMapping("/category/{id}")
    public int obsoleteCategory(@PathVariable Long id) {
        return categoryService.obsoleteCategory(id);
    }


    @GetMapping("/category/{id}/detail")
    public CategoryDTO getCategoryDetail(@PathVariable Long id) {
        return categoryService.getCategoryDetail(id);
    }

    @PutMapping("/category/{id}/attributes")
    public void updateCategoryAttributes(@PathVariable Long id, @RequestBody UpdateCategoryAttributesParam updateCategoryAttributesParam) {
        updateCategoryAttributesParam.setId(id);
        categoryService.updateCategoryAttributes(updateCategoryAttributesParam);
    }

}
