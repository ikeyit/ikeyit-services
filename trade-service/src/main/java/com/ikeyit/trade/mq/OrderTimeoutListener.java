package com.ikeyit.trade.mq;


import com.ikeyit.trade.service.OrderService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 响应订单超时消息
 */
@RocketMQMessageListener(topic = "trade_order",selectorExpression = "timeout", consumerGroup = "trade_order_timeout")
@Component
public class OrderTimeoutListener implements RocketMQListener<Long> {

    private static Logger log = LoggerFactory.getLogger(OrderTimeoutListener.class);

    @Autowired
    OrderService orderService;


    @Override
    public void onMessage(Long orderId) {
        log.debug("[MQ]订单超时消息!orderId: {}", orderId);
        orderService.timeoutOrder(orderId);
    }
}
