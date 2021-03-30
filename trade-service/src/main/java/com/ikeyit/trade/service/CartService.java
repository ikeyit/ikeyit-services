package com.ikeyit.trade.service;

import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.trade.domain.CartItem;
import com.ikeyit.trade.dto.CartItemDTO;
import com.ikeyit.trade.exception.TradeErrorCode;
import com.ikeyit.trade.feign.ProductClient;
import com.ikeyit.trade.feign.SkuDTO;
import com.ikeyit.trade.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车服务
 */
@Service
public class CartService {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ProductClient productClient;


    /**
     * 向用户购物车里添加SKU
     *
     * @param skuId
     * @param quantity
     * @return
     */
    public CartItemDTO addCartItem(Long skuId, Integer quantity) {
        //如果SKU已经存在，就增加数量
        //如果不存在，新建
        Long userId = authenticationService.getCurrentUserId();
        CartItem cartItem = cartItemRepository.getByUserIdAndSkuId(userId, skuId);
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setSkuId(skuId);
            cartItem.setQuantity(quantity);
            cartItemRepository.create(cartItem);
        } else {
            cartItem.setQuantity(quantity + cartItem.getQuantity());
            cartItemRepository.update(cartItem);
        }

        return buildCartItemDto(cartItem);
    }


    public CartItemDTO createTempCartItem(Long skuId, Integer quantity) {
        Long userId = authenticationService.getCurrentUserId();
        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setSkuId(skuId);
        cartItem.setQuantity(quantity);
        return buildCartItemDto(cartItem);
    }

    private CartItemDTO buildCartItemDto(CartItem cartItem) {
        SkuDTO skuDetail = productClient.getSkuDetail(cartItem.getSkuId());
        CartItemDTO cartItemDto = new CartItemDTO();
        cartItemDto.setId(cartItem.getId());
        cartItemDto.setSkuId(skuDetail.getId());
        cartItemDto.setProductId(skuDetail.getProductId());
        cartItemDto.setSellerId(skuDetail.getSellerId());
        cartItemDto.setImage(skuDetail.getImage());
        cartItemDto.setTitle(skuDetail.getTitle());
        cartItemDto.setQuantity(cartItem.getQuantity());
        cartItemDto.setStatus(cartItem.getStatus());
        cartItemDto.setPrice(skuDetail.getPrice());
        cartItemDto.setInitialPrice(cartItem.getPrice());
        String attrs = skuDetail
                .getAttributes()
                .stream()
                .map(attrPair -> attrPair.getVal())
                .collect(Collectors.joining(","));
        cartItemDto.setSkuName(attrs);
        return cartItemDto;
    }

    public List<CartItemDTO> getCartItems() {
        Long userId = authenticationService.getCurrentUserId();
        List<CartItem> cartItems = cartItemRepository.getByUserId(userId);
        List<CartItemDTO> cartItemDetails = cartItems.stream().map(cartItem -> {
            return buildCartItemDto(cartItem);
        }).collect(Collectors.toList());
        return cartItemDetails;
    }



    public CartItemDTO getCartItem(Long id) {
        Long userId = authenticationService.getCurrentUserId();
        CartItem cartItem = getCartItemSafely(userId, id);
        return buildCartItemDto(cartItem);

    }

    public void deleteCartItem(Long id) {
        Long userId = authenticationService.getCurrentUserId();
        CartItem cartItem = getCartItemSafely(userId, id);
        cartItemRepository.delete(cartItem.getId());
    }

    @Transactional
    public void deleteCartItems(Long[] ids) {
        Long userId = authenticationService.getCurrentUserId();
        for (Long id : ids) {
            CartItem cartItem = getCartItemSafely(userId, id);
            cartItemRepository.delete(cartItem.getId());
        }
    }


    @Transactional
    public void updateCartItem(Long id, Long skuId, Integer quantity) {
        if (quantity < 1)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);

        Long userId = authenticationService.getCurrentUserId();
        CartItem cartItem = getCartItemSafely(userId, id);
        cartItem.setQuantity(quantity);
        //SKU变了
        if (skuId != null && !skuId.equals(cartItem.getSkuId())) {
            cartItem.setSkuId(skuId);
            //删除重复的item，前端也需要删除保持同步
            cartItemRepository.deleteByUserIdAndSkuId(userId, skuId);
        }

        cartItemRepository.update(cartItem);
    }


    private CartItem getCartItemSafely(Long userId, Long id) {
        CartItem cartItem = cartItemRepository.getById(id);
        if (cartItem == null)
            throw new BusinessException(TradeErrorCode.CART_ITEM_NOT_FOUND, id);
        if (!cartItem.getUserId().equals(userId))
            throw new BusinessException(TradeErrorCode.CART_ITEM_ILLEGAL_ACCESS);

        return cartItem;
    }
}
