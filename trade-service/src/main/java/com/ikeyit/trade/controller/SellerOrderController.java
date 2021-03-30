package com.ikeyit.trade.controller;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.trade.dto.OrderDTO;
import com.ikeyit.trade.dto.SellerGetOrdersParam;
import com.ikeyit.trade.dto.ShipOrderParam;
import com.ikeyit.trade.service.SellerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
public class SellerOrderController {

    @Autowired
    SellerOrderService sellerOrderService;


    /**
     * 卖家获取订单详情
     * @param id
     * @return
     */
    @GetMapping("/order/{id}")
    public OrderDTO getOrder(@PathVariable Long id) {
        return sellerOrderService.getOrder(id);
    }


    /**
     * 卖家获取查询订单列表
     * @return
     */
    @GetMapping("/orders")
    public Page<OrderDTO> getOrders(SellerGetOrdersParam sellerGetOrdersParam,
                                    @RequestParam(required = false) String sort,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        return sellerOrderService.getOrders(sellerGetOrdersParam, sort, new PageParam(page, pageSize));
    }


    /**
     * 卖家发货
     * @param id
     */
    @PostMapping("/order/{id}/ship")
    public void shipOrder(@PathVariable Long id, @RequestBody ShipOrderParam shipOrderParam) {
        shipOrderParam.setId(id);
        sellerOrderService.shipOrder(shipOrderParam);
    }


    @PostMapping("/order/{id}/memo")
    public int updateMemo(@PathVariable Long id, String memo) {
        return sellerOrderService.updateSellerMemo(id, memo);
    }
}
