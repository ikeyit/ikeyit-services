package com.ikeyit.product.controller;


import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.product.domain.ShopBasicInfo;
import com.ikeyit.product.dto.*;
import com.ikeyit.product.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ShopController {

    @Autowired
    ShopService shopService;

    @GetMapping("seller/shop_basic_info")
    public ShopBasicInfo getShopBasicInfo() {
        return shopService.getShopBasicInfo();
    }

    @PutMapping("seller/shop_basic_info")
    public int updateShopBasicInfo(@RequestBody UpdateShopBasicInfoParam updateShopBasicInfoParam) {
        return shopService.updateShopBasicInfo(updateShopBasicInfoParam);
    }

    @GetMapping("seller/shop_page/{id}")
    public ShopPageDTO getShopPage(@PathVariable Long id) {
        return shopService.getShopPage(id);
    }

    @PostMapping("seller/shop_page")
    public int createShopPage(@RequestBody CreateShopPageParam createShopPageParam) {
        return shopService.createShopPage(createShopPageParam);
    }

    @GetMapping("seller/shop_pages")
    public Page<ShopPageDTO> getShopPages(Integer type, Integer status, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        return shopService.getShopPages(type, status, new PageParam(page, pageSize));
    }

    @PutMapping("seller/shop_page/{id}")
    public int updateShopPage(@PathVariable Long id,  @RequestBody UpdateShopPageParam updateShopPageParam) {
        updateShopPageParam.setId(id);
        return shopService.updateShopPage(updateShopPageParam);
    }


    @GetMapping("shop/home/seller/{id}")
    public ShopHomeDTO getShopHomePage(@PathVariable Long id) {
        return shopService.getShopHome(id);
    }

    @GetMapping("shop_pages")
    public Page<ShopPageDTO> getShopPages(Long sellerId, Integer type, Integer status, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        return shopService.getShopPages(sellerId, type, status, new PageParam(page, pageSize));
    }

}
