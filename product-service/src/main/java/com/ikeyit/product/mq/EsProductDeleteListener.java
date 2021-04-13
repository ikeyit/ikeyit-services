package com.ikeyit.product.mq;

import com.ikeyit.product.service.ProductSearchService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@RocketMQMessageListener(topic = "product", selectorExpression = "offSale", consumerGroup = "es_product_delete")
@Component
public class EsProductDeleteListener implements RocketMQListener<Long> {

    private static Logger log = LoggerFactory.getLogger(EsProductDeleteListener.class);

    @Autowired
    ProductSearchService productSearchService;

    @Override
    public void onMessage(Long productId) {
        log.info("[MQ]商品搜索服务，收到商品下架消息！更新ES！productId: {}", productId);
        productSearchService.deleteProduct(productId);
    }
}
