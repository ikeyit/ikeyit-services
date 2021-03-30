package com.ikeyit.trade.mq;


import com.ikeyit.pay.mq.RefundSuccessMessage;
import com.ikeyit.trade.service.Constants;
import com.ikeyit.trade.service.RefundService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 响应订单超时消息
 */
@RocketMQMessageListener(topic = "pay_refund_success", selectorExpression = Constants.REFUND_ORDER_TYPE_AFTER_SALE, consumerGroup = "pay_refund_success_" + Constants.REFUND_ORDER_TYPE_AFTER_SALE)
@Component
public class RefundSuccessListener implements RocketMQListener<RefundSuccessMessage> {

    private static Logger log = LoggerFactory.getLogger(RefundSuccessListener.class);

    @Autowired
    RefundService refundService;

    @Override
    public void onMessage(RefundSuccessMessage refundSuccessMessage) {
        log.debug("[MQ]退款成功! refundType: {}, refundId: {}", refundSuccessMessage.getRefundType(), refundSuccessMessage.getRefundId());
        refundService.handleRefunded(refundSuccessMessage.getRefundId(),
                refundSuccessMessage.getPayWay(),
                refundSuccessMessage.getRefundAmount(),
                refundSuccessMessage.getSuccessTime()
              );
    }
}
