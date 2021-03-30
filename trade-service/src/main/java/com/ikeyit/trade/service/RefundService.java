package com.ikeyit.trade.service;

import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.utils.JsonUtils;
import com.ikeyit.trade.domain.Order;
import com.ikeyit.trade.domain.Refund;
import com.ikeyit.trade.domain.RefundNegotiation;
import com.ikeyit.trade.exception.TradeErrorCode;
import com.ikeyit.trade.repository.OrderItemRepository;
import com.ikeyit.trade.repository.OrderRepository;
import com.ikeyit.trade.repository.RefundNegotiationRepository;
import com.ikeyit.trade.repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@Service
public class RefundService {

    @Autowired
    RefundRepository refundRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    RefundNegotiationRepository refundNegotiationRepository;

    /**
     *
     * @param refundId
     * @param refundAmount
     */
    @Transactional
    public void handleRefunded(Long refundId, String payWay, BigDecimal refundAmount, LocalDateTime successTime) {
        Refund refund = refundRepository.getById(refundId);
        if (refund == null) // 不应该出现，出现说明存在bug
            throw new BusinessException(TradeErrorCode.REFUND_NOT_FOUND, refundId);

        Integer status = refund.getStatus();
        //防重
        if (Refund.STATUS_SUCCESS.equals(status))
            return;

        if (!Refund.STATUS_SELLER_AGREED_REFUND.equals(status))
            throw new BusinessException(TradeErrorCode.REFUND_INVALID_STATUS, refundId);

        refund.setStatus(Refund.STATUS_SUCCESS);
        if (refundRepository.updateStatus(refund, status) != 1)
            //其它线程有可能已经修改状态为SUCCESS， 返回
            //这里假设状态只存在这个状态迁移STATUS_SELLER_AGREED_REFUND->STATUS_SUCCESS
            return;

        orderItemRepository.updateStatusById(refund.getOrderItemId(), Order.STATUS_CLOSED);
        //全部都是CLOSED
        if (orderItemRepository.countStatusIsNot(refund.getOrderId(), Order.STATUS_CLOSED) == 0) {
            Order order = orderRepository.getById(refund.getOrderId());
            Integer orderStatus = order.getStatus();
            if (order == null) // 不应该出现，出现说明存在bug
                throw new BusinessException(TradeErrorCode.ORDER_NOT_FOUND, refund.getOrderId());
            order.setStatus(Order.STATUS_CLOSED);
            order.setCloseReason(Order.CLOSE_REASON_REFUNDED);
            order.setFinishTime(LocalDateTime.now());
            if (orderRepository.updateStatus(order, orderStatus) != 1) {
                throw new BusinessException(TradeErrorCode.ORDER_INVALID_STATUS, refundId);
            }
        }

        LinkedHashMap<String, Object> message = new LinkedHashMap<>();
        message.put("退款金额：", refundAmount);
        message.put("支付方式：", payWay);
        message.put("退款时间：", successTime);
        saveRefundNegotiation(refundId, "退款成功", message);
    }

    private void saveRefundNegotiation(Long refundId, String operation, LinkedHashMap message) {
        RefundNegotiation refundNegotiation = new RefundNegotiation();
        refundNegotiation.setRefundId(refundId);
        refundNegotiation.setOperatorId(0L);
        refundNegotiation.setOperator("system");
        refundNegotiation.setOperation(operation);
        refundNegotiation.setMessage(JsonUtils.writeValueAsString(message));
        refundNegotiationRepository.create(refundNegotiation);
    }
}
