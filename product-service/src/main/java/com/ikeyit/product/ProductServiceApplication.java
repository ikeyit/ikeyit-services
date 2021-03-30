package com.ikeyit.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages={"com.ikeyit.product", "com.ikeyit.cms", "com.ikeyit.media"})
@MapperScan(basePackages={"com.ikeyit.product.repository", "com.ikeyit.cms.repository", "com.ikeyit.media.repository"})
@EnableDiscoveryClient
@EnableCaching
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}