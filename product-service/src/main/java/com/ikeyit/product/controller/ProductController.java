package com.ikeyit.product.controller;

import com.ikeyit.product.dto.ProductDTO;
import com.ikeyit.product.dto.ReduceStockParam;
import com.ikeyit.product.dto.SkuDTO;
import com.ikeyit.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("product/{id}")
    public ProductDTO getProduct(@PathVariable Long id) {
        return productService.getProductDetail(id);
    }


    @GetMapping("products")
    public List<ProductDTO> getProductsByShopId(@RequestParam Long sellerId,
                                                @RequestParam(required = false) String sort) {
        return productService.getProductsByShop(sellerId, sort);
    }

    @GetMapping("sku/{id}")
    public SkuDTO getSkuDetail(@PathVariable Long id) {
        return productService.getSkuDetail(id);
    }

    @PutMapping("sku/stock")
    public void reduceOrderStock(@RequestBody ReduceStockParam reduceStockParam) {
        productService.reduceOrderStock(reduceStockParam);
    }
}
