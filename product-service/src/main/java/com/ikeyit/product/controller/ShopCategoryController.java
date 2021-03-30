package com.ikeyit.product.controller;

import com.ikeyit.product.dto.*;
import com.ikeyit.product.service.ShopCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShopCategoryController {
    @Autowired
    ShopCategoryService shopCategoryService;

    @GetMapping("seller/shop_categories")
    public List<ShopCategoryDTO> getShopCategories() {
        return shopCategoryService.getShopCategories();
    }

    @PostMapping("seller/shop_category")
    public void createShopCategory(@RequestBody EditShopCategoryParam editShopCategoryParam) {
        shopCategoryService.createShopCategory(editShopCategoryParam);
    }

    @PutMapping("seller/shop_category/{id}")
    public void updateShopCategory(@PathVariable Long id, @RequestBody EditShopCategoryParam editShopCategoryParam) {
        editShopCategoryParam.setId(id);
        shopCategoryService.updateShopCategory(editShopCategoryParam);
    }


    @PutMapping("seller/shop_category/order")
    public void orderShopCategories(@RequestBody OrderShopCategoriesParam orderShopCategoriesParam) {
        shopCategoryService.orderShopCategories(orderShopCategoriesParam);
    }


    @DeleteMapping("seller/shop_category/{id}")
    public void deleteShopCategory(@PathVariable Long id) {
        shopCategoryService.deleteShopCategory(id);
    }


    @GetMapping("shop_categories")
    public List<ShopCategoryDTO> getShopCategories(Long sellerId) {
        return shopCategoryService.getShopCategories(sellerId);
    }

    @GetMapping("seller/shop_category_products")
    public List<ProductDTO> getShopCategoryProducts(Long shopCategoryId2) {
        return shopCategoryService.getShopCategoryProducts(shopCategoryId2);
    }


    @PostMapping("seller/shop_category_product")
    public void addProductIntoShopCategory(@RequestBody AddProductIntoShopCategoryParam addProductIntoShopCategoryParam) {
        shopCategoryService.addProductIntoShopCategory(addProductIntoShopCategoryParam);
    }

    @DeleteMapping("seller/shop_category_product")
    public void removeProductIntoShopCategory(@RequestBody RemoveProductIntoShopCategoryParam removeProductIntoShopCategoryParam) {
        shopCategoryService.removeProductIntoShopCategory(removeProductIntoShopCategoryParam);
    }
}
