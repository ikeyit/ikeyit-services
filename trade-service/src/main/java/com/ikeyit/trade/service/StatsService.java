package com.ikeyit.trade.service;

import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.trade.domain.Order;
import com.ikeyit.trade.dto.HomeStatsDTO;
import com.ikeyit.trade.repository.OrderRepository;
import com.ikeyit.trade.repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class StatsService {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    RefundRepository refundRepository;

    public HomeStatsDTO getHomeStats() {
        Long sellerId = authenticationService.getCurrentUserId();
        HomeStatsDTO homeStatsDTO = new HomeStatsDTO();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayZeroTime = now.truncatedTo(ChronoUnit.DAYS);
        homeStatsDTO.setPaidOrderCount(orderRepository.countBySellerStatus(sellerId, Order.STATUS_PAID));
        //TODO 暂时HARDCODE 最多7天时间订单自动关闭
        homeStatsDTO.setWaitBuyerPayOrderCount(orderRepository.countBySellerStatusDuring(sellerId, Order.STATUS_WAIT_BUYER_PAY, LocalDateTime.now().minusDays(7), now));
        homeStatsDTO.setTodayOrderCount(orderRepository.countBySellerDuring(sellerId, todayZeroTime, now));
        homeStatsDTO.setTodayTurnover(orderRepository.sumPaymentAmountBySeller(sellerId, todayZeroTime, now));
        return homeStatsDTO;
    }
}
