package com.ikeyit.pay.mq;


import com.ikeyit.pay.service.PayService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 响应订单超时消息
 */
@RocketMQMessageListener(topic = "pay_refund_submit", consumerGroup = "pay_refund_submit")
@Component
public class RefundSubmitListener implements RocketMQListener<RefundSubmitMessage> {

    private static Logger log = LoggerFactory.getLogger(RefundSubmitListener.class);

    @Autowired
    PayService payService;

    @Override
    public void onMessage(RefundSubmitMessage refundSubmitMessage) {
        log.debug("[MQ]请求支付系统退款消息!refundType: {}, refundId: {}", refundSubmitMessage.getRefundType(), refundSubmitMessage.getRefundId());
        payService.submitRefundOrder(refundSubmitMessage.getRefundType(), refundSubmitMessage.getRefundId());
    }
}
