package com.ikeyit.trade.mq;


import com.ikeyit.pay.mq.PaySuccessMessage;
import com.ikeyit.trade.service.Constants;
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
@RocketMQMessageListener(topic = "pay_success", selectorExpression = Constants.PAY_ORDER_TYPE, consumerGroup = "pay_success_" + Constants.PAY_ORDER_TYPE)
@Component
public class PaySuccessListener implements RocketMQListener<PaySuccessMessage> {

    private static Logger log = LoggerFactory.getLogger(PaySuccessListener.class);

    @Autowired
    OrderService orderService;

    @Override
    public void onMessage(PaySuccessMessage paySuccessMessage) {
        log.debug("[MQ]支付成功! orderType: {}, orderId: {}", paySuccessMessage.getOrderType(), paySuccessMessage.getOrderId());
        orderService.handlePaid(
                paySuccessMessage.getOrderId(),
                paySuccessMessage.getPayWay(),
                paySuccessMessage.getSuccessTime(),
                paySuccessMessage.getPayOrderId());
    }
}
