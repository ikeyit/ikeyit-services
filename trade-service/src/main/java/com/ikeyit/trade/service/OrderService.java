package com.ikeyit.trade.service;


import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.pay.dto.CreateRefundOrderParam;
import com.ikeyit.pay.service.PayService;
import com.ikeyit.trade.domain.Order;
import com.ikeyit.trade.domain.OrderItem;
import com.ikeyit.trade.domain.OrderLog;
import com.ikeyit.trade.exception.TradeErrorCode;
import com.ikeyit.trade.mq.OrderMessage;
import com.ikeyit.trade.repository.OrderItemRepository;
import com.ikeyit.trade.repository.OrderLogRepository;
import com.ikeyit.trade.repository.OrderRepository;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;

/**
 * 系统级的订单服务, 处理订单超时，支付回调等等
 */
@Service
public class OrderService {

    private static Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderLogRepository orderLogRepository;

    @Autowired
    PayService payService;

    @Autowired
    RocketMQTemplate rocketMQTemplate;



    /**
     * 超时取消订单, 系统取消
     * @param orderId
     */
    @Transactional
    public void timeoutOrder(Long orderId) {
        Order order = orderRepository.getById(orderId);
        if (order == null)
            return;

        Integer status = order.getStatus();
        if (!status.equals(Order.STATUS_WAIT_BUYER_PAY)) {
            log.debug("订单状态不为等待支付，跳过超时关闭订单！orderId: {}", orderId);
            return;
        }

        order.setFinishTime(LocalDateTime.now());
        order.setStatus(Order.STATUS_CLOSED);
        order.setCloseReason(Order.CLOSE_REASON_EXPIRED);
        //并发出现问题，则抛出异常，MQ会重发消息，最终会正确处理
        if (orderRepository.updateStatus(order, status) != 1)
            throw new BusinessException(TradeErrorCode.ORDER_INVALID_STATUS, orderId);

        orderItemRepository.updateStatusByOrderId(orderId, Order.STATUS_CLOSED);
        saveOrderLog(order, "超时取消订单", "超时取消订单");
        rocketMQTemplate.syncSend("trade_order:cancelled",
                MessageBuilder.withPayload(orderId).setHeader("KEYS", orderId).build(),
                rocketMQTemplate.getProducer().getSendMsgTimeout());
    }

    private void saveOrderLog(Order order, String operation, String message) {
        //记录订单流水
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(order.getId());
        orderLog.setOrderStatus(order.getStatus());
        orderLog.setOperator("系统");
        orderLog.setOperation(operation);
        orderLog.setMessage(message);
        orderLogRepository.create(orderLog);
    }


    public Order getOrder(Long orderId) {
        if (orderId == null) {
            log.error("订单不应该为空，可能存在BUG, orderId: {}", orderId);
            throw new BusinessException(TradeErrorCode.ORDER_NOT_FOUND, "null");
        }
        Order order = orderRepository.getById(orderId);
        if (order == null) {
            log.error("订单不应该为空，可能存在BUG, orderId: {}", orderId);
            throw new BusinessException(TradeErrorCode.ORDER_NOT_FOUND, orderId.toString());
        }
        return order;
    }

    @Transactional
    public void handlePaid(Long orderId, String payWay, LocalDateTime successTime, Long payOrderId) throws IllegalStateException {
        Order order = getOrder(orderId);
        Integer status = order.getStatus();
        //订单已经是支付成功状态立即返回
        if (Order.STATUS_PAID.equals(status))
            return;

        if (Order.STATUS_CLOSED.equals(status)) {
            //超时订单被关闭，或者其它原因订单被关闭
            CreateRefundOrderParam createRefundOrderParam = new CreateRefundOrderParam();
            createRefundOrderParam.setRefundType(Constants.REFUND_ORDER_TYPE_SYSTEM);
            createRefundOrderParam.setRefundId(orderId);
            createRefundOrderParam.setOrderType(Constants.PAY_ORDER_TYPE);
            createRefundOrderParam.setOrderId(orderId);
            createRefundOrderParam.setRefundAmount(order.getPaymentAmount());
            createRefundOrderParam.setReason("订单超时");
            payService.createRefundOrder(createRefundOrderParam);
            return;
        }

        if (!Order.STATUS_WAIT_BUYER_PAY.equals(status)) {
            log.error("订单状态不正确，期待为等待付款，实际：orderId: {}, status: {}", orderId, status);
            throw new BusinessException(TradeErrorCode.ORDER_NOT_FOUND, orderId);
        }

        //订单为等待付款，则更新订单为已付款
        order.setPayWay(payWay);
        order.setPayTime(successTime);
        order.setPayOrderId(payOrderId);
        order.setStatus(Order.STATUS_PAID);

        if (orderRepository.updateStatus(order, status) != 1)
            //更新数据库时还有可能订单状态被其它线程修改，抛出异常，让MQ重发消息，重新执行
            throw new BusinessException(TradeErrorCode.ORDER_INVALID_STATUS, orderId);

        orderItemRepository.updateStatusByOrderId(orderId, Order.STATUS_PAID);
        saveOrderLog(order, "支付成功", "支付方式：" + order.getPayWay()
                + ", 支付订单ID：" + payOrderId);


        //发送消息
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOrderId(order.getId());
        orderMessage.setQuantity(order.getTotalQuantity());
        orderMessage.setBuyerId(order.getBuyerId());
        orderMessage.setSellerId(order.getSellerId());
        orderMessage.setOrderStatus(order.getStatus());
        orderMessage.setTotalAmount(order.getGoodsAmount());
        OrderItem orderItem = orderItemRepository.getFirstByOrderId(orderId);
        orderMessage.setTitle(orderItem.getTitle());
        orderMessage.setImage(orderItem.getImage());


        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
            public void afterCommit() {
                log.debug("事务结束，发送消息到MQ中");
                Message message = MessageBuilder
                        .withPayload(orderMessage)
                        .setHeader("KEYS", orderId.toString())
                        .build();
                SendResult sendResult = rocketMQTemplate.syncSend("trade_order:paid", message);
            }
        });

    }

}
