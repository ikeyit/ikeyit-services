package com.ikeyit.product.controller;

import com.ikeyit.common.domain.Page;
import com.ikeyit.product.dto.ProductDTO;
import com.ikeyit.product.dto.SearchProductParam;
import com.ikeyit.product.service.ProductSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductSearchController {

    @Autowired
    ProductSearchService productSearchService;

    @GetMapping("/search/_index/product")
    public void createIndex() {
        productSearchService.createProductIndex();
    }

    @GetMapping("/search/_index/search_record")
    public void createSearchRecord() {
        productSearchService.createSearchRecordIndex();
    }

    @GetMapping("/search/hot_queries")
    public List<String> getHotQueries(@RequestParam(required = false) Long sellerId) {
        return productSearchService.getHotQueries(sellerId);
    }

    @GetMapping("/search/latest_queries")
    public List<String> getLatestUserQueries(@RequestParam(required = false) Long userId) {
        return productSearchService.getLatestUserQueries(userId);
    }

    @GetMapping("/search/add_product/{id}")
    public void addProduct(@PathVariable Long id) {
        productSearchService.saveProduct(id);
    }

    @GetMapping("/search/product")
    public Page<ProductDTO> searchProduct(SearchProductParam searchProductParam) {
        return productSearchService.search(searchProductParam);
    }

}
