package com.ikeyit.product.mq;

import com.ikeyit.product.service.ProductSearchService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RocketMQMessageListener(topic = "product", selectorExpression = "create || update || onSale", consumerGroup = "es_product_update")
@Component
public class EsProductUpdateListener implements RocketMQListener<Long> {

    private static Logger log = LoggerFactory.getLogger(EsProductUpdateListener.class);

    @Autowired
    ProductSearchService productSearchService;

    @Override
    public void onMessage(Long productId) {
        log.info("[MQ]商品搜索服务，收到商品更新消息！更新ES！productId: {}", productId);
        productSearchService.saveProduct(productId);
    }
}
