package com.ikeyit.trade.controller;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.trade.dto.AgreeReturnParam;
import com.ikeyit.trade.dto.RefundDTO;
import com.ikeyit.trade.dto.RejectRefundParam;
import com.ikeyit.trade.dto.SellerGetRefundsParam;
import com.ikeyit.trade.service.SellerRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
public class SellerRefundController {

    @Autowired
    SellerRefundService sellerRefundService;


    @GetMapping("/refunds")
    public Page<RefundDTO> getRefunds(SellerGetRefundsParam sellerGetRefundsParam, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
       return sellerRefundService.getRefunds(sellerGetRefundsParam, new PageParam(page, pageSize));
    }

    @GetMapping("/refund/{id}")
    public RefundDTO getRefund(@PathVariable Long id) {
        return sellerRefundService.getRefundDetail(id);
    }

    @PutMapping("/refund/{id}/agree_refund")
    public void agreeRefund(@PathVariable Long id) {
        sellerRefundService.agreeRefund(id);
    }

    @PutMapping("/refund/{id}/agree_return")
    public void agreeReturn(@PathVariable Long id, @RequestBody AgreeReturnParam agreeReturnParam) {
        agreeReturnParam.setId(id);
        sellerRefundService.agreeReturn(agreeReturnParam);
    }

    @PutMapping("/refund/{id}/reject")
    public void rejectRefund(@PathVariable Long id, @RequestBody RejectRefundParam rejectRefundParam) {
        rejectRefundParam.setRefundId(id);
        sellerRefundService.rejectRefund(rejectRefundParam);
    }

}
