package com.ikeyit.trade.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("sku/{id}")
    SkuDTO getSkuDetail(@PathVariable Long id);


    @PutMapping("sku/stock")
    void reduceStock(ReduceStockParam reduceStockParam);
}
