package com.ikeyit.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages={"com.ikeyit.product", "com.ikeyit.cms", "com.ikeyit.media", "com.ikeyit.mqhelper"})
@MapperScan(basePackages={"com.ikeyit.product.repository", "com.ikeyit.cms.repository", "com.ikeyit.media.repository", "com.ikeyit.mqhelper"})
@EnableDiscoveryClient
@EnableScheduling
@EnableCaching
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}