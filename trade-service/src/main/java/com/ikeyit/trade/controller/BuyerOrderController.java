package com.ikeyit.trade.controller;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.trade.dto.OrderConfirmationDTO;
import com.ikeyit.trade.dto.OrderDTO;
import com.ikeyit.trade.dto.OrderParam;
import com.ikeyit.trade.service.BuyerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
public class BuyerOrderController {

    @Autowired
    BuyerOrderService buyerOrderService;

    @GetMapping("/orders")
    public Page<OrderDTO> getOrders(Integer status, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        return buyerOrderService.getOrders(status, new PageParam(page, pageSize));
    }


    @PostMapping("/order/confirmation")
    public OrderConfirmationDTO createOrderConfirmation(
            @RequestParam(required = false) Long[] cartItemIds,
            @RequestParam(required = false) Long skuId,
            @RequestParam(required = false, defaultValue = "1") Integer quantity) {
        if (cartItemIds != null && cartItemIds.length > 0) {
            return buyerOrderService.createOrderConfirmation(cartItemIds);
        } else if (skuId != null) {
            return buyerOrderService.createOrderConfirmation(skuId, quantity);
        } else {
            throw new IllegalArgumentException("参数不正确");
        }
    }


    /**
     * 创建订单
     * @param orderParam
     * @return
     */
    @PostMapping("/order")
    public Long createOrder(@RequestBody OrderParam orderParam) {
        return buyerOrderService.createOrder(orderParam);
    }

    /**
     * 获取订单详情
     * @param id
     * @return
     */
    @GetMapping("/order/{id}")
    public OrderDTO getOrder(@PathVariable Long id) {
        return buyerOrderService.getOrder(id);
    }



    /**
     * 用户确认收货
     * @param id
     */
    @PostMapping("/order/{id}/finish")
    public void finishOrder(@PathVariable Long id) {
        buyerOrderService.finishOrder(id);
    }

    /**
     * 用户取消订单
     * @param id
     */
    @PostMapping("/order/{id}/cancel")
    public void cancelOrder(@PathVariable Long id) {
        buyerOrderService.cancelOrder(id);
    }


    /**
     * 用户请求支付
     * @param id
     */
    @PostMapping("/order/{id}/pay_request/{payWay}")
    public Object payOrderRequest(@PathVariable Long id, @PathVariable String payWay, @RequestBody Map<String, Object> params) {
        return buyerOrderService.submitPayOrder(id, payWay, params);
    }

    @GetMapping("/order/{id}/pay_check/{payWay}")
    public void payOrderCheck(@PathVariable Long id, @PathVariable String payWay) {
        buyerOrderService.payOrderCheck(id, payWay);
    }
}
