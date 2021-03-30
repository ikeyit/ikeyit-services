package com.ikeyit.product.controller;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.product.dto.*;
import com.ikeyit.product.service.SellerProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SellerProductController {
    @Autowired
    SellerProductService sellerProductService;

    @GetMapping("seller/products")
    public Page<ProductDTO> getProducts(SellerGetProductsParam sellerGetProductsParam, String sort, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        return sellerProductService.getProducts(sellerGetProductsParam, sort, new PageParam(page, pageSize));
    }

    @GetMapping("seller/product/{id}")
    public SellerProductDTO getProduct(@PathVariable Long id) {
        return sellerProductService.getProduct(id);
    }


    @PostMapping("seller/product")
    public void createProduct(@RequestBody EditProductParam editProductParam) {
        sellerProductService.createProduct(editProductParam);

    }

    @PutMapping("seller/product/{id}")
    public void updateProduct(@PathVariable Long id,  @RequestBody EditProductParam editProductParam) {
        editProductParam.setId(id);
        sellerProductService.updateProduct(editProductParam);

    }


    @PutMapping("seller/product/{id}/on_sale")
    public void setOnSale(@PathVariable Long id) {
        sellerProductService.setOnSale(id);

    }

    @PutMapping("seller/product/{id}/off_sale")
    public void setOffSale(@PathVariable Long id) {
        sellerProductService.setOffSale(id);
    }


    @PutMapping("seller/sku/stock")
    public void setOffSale(List<UpdateSkuStockParam> updateSkuStockParams) {
        sellerProductService.updateSkuStock(updateSkuStockParams);
    }
}

