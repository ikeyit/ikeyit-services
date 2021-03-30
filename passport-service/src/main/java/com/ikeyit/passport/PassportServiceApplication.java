package com.ikeyit.passport;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages={"com.ikeyit.passport", "com.ikeyit.message", "com.ikeyit.user"})
@MapperScan(basePackages = {"com.ikeyit.passport.repository", "com.ikeyit.message.repository", "com.ikeyit.user.repository"})
@EnableTransactionManagement(proxyTargetClass = true)
@EnableDiscoveryClient
public class PassportServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PassportServiceApplication.class, args);
    }

}
