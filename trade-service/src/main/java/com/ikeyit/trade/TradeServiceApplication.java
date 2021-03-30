package com.ikeyit.trade;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication(scanBasePackages={"com.ikeyit.pay", "com.ikeyit.trade", "com.ikeyit.mqhelper"})
@EnableFeignClients(basePackages={"com.ikeyit.pay.feign", "com.ikeyit.trade.feign"})
@MapperScan(basePackages = {"com.ikeyit.pay.repository", "com.ikeyit.trade.repository", "com.ikeyit.mqhelper"})
@EnableTransactionManagement(proxyTargetClass = true)
@EnableScheduling
@EnableDiscoveryClient
public class TradeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeServiceApplication.class, args);
    }
}
