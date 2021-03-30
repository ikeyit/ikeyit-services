package com.ikeyit.trade.controller;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.trade.domain.Refund;
import com.ikeyit.trade.dto.CreateRefundParam;
import com.ikeyit.trade.dto.RefundDTO;
import com.ikeyit.trade.dto.ShipReturnParam;
import com.ikeyit.trade.dto.UpdateRefundParam;
import com.ikeyit.trade.service.BuyerRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class BuyerRefundController {

    @Autowired
    BuyerRefundService refundService;

    @GetMapping("/refund/application")
    public RefundDTO getRefundApplication(Long orderItemId) {
        return refundService.getRefundApplication(orderItemId);
    }

    @PostMapping("/refund")
    public RefundDTO createRefund(@RequestBody CreateRefundParam createRefundParam) {
        return refundService.createRefund(createRefundParam);
    }

    @PutMapping("/refund/{id}")
    public int updateRefund(@PathVariable Long id, @RequestBody UpdateRefundParam updateRefundParam) {
        updateRefundParam.setRefundId(id);
        return refundService.updateRefund(updateRefundParam);
    }

    @GetMapping("/refunds")
    public Page<Refund> getRefunds(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
       return refundService.getRefunds(new PageParam(page, pageSize));
    }

    @GetMapping("/refund/{id}")
    public RefundDTO getRefund(@PathVariable Long id) {
        return refundService.getRefundDetail(id);
    }

    @PostMapping("/refund/{id}/ship")
    public void shipReturn(@PathVariable Long id, @RequestBody ShipReturnParam shipReturnParam) {
        shipReturnParam.setId(id);
        refundService.shipReturn(shipReturnParam);
    }

    @PostMapping("/refund/{id}/cancel")
    public void cancelRefund(@PathVariable Long id) {
        refundService.cancelRefund(id);
    }

}
