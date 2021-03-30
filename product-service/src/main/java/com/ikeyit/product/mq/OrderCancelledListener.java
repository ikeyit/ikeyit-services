package com.ikeyit.product.mq;

import com.ikeyit.product.service.ProductService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@RocketMQMessageListener(topic = "trade_order",selectorExpression = "cancelled",consumerGroup = "trade_order_stock")
@Component
public class OrderCancelledListener implements RocketMQListener<Long> {

    private static Logger log = LoggerFactory.getLogger(OrderCancelledListener.class);

    @Autowired
    ProductService productService;

    @Override
    public void onMessage(Long orderId) {
        log.info("[MQ]收到订单取消消息！释放库存！orderId: {}", orderId);
        productService.addOrderStock(orderId);
    }
}
