package com.ikeyit.trade.controller;


import com.ikeyit.trade.dto.CartItemDTO;
import com.ikeyit.trade.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/cart/items")
    public List<CartItemDTO> getCartItems() {
        return cartService.getCartItems();
    }

    @GetMapping("/cart/item/{id}")
    public CartItemDTO getCartItem(@PathVariable Long id) {
        return cartService.getCartItem(id);
    }

    @PostMapping("/cart/item")
    public void addCartItem(Long skuId, Integer quantity) throws ExecutionException, InterruptedException {
        cartService.addCartItem(skuId, quantity);
    }

    @DeleteMapping("/cart/item/{id}")
    public void deleteCartItem(@PathVariable Long id) {
        cartService.deleteCartItem( id);
    }

    @PutMapping("/cart/item/{id}")
    public void updateCartItem(@PathVariable Long id, @RequestParam(required=false) Long skuId, Integer quantity) {
        cartService.updateCartItem(id, skuId, quantity);
    }


}
