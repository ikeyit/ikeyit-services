package com.ikeyit.trade.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passport-service", contextId="UserClient")
public interface UserClient {
    @GetMapping("/user/{id}")
    UserDTO getUserById(@PathVariable Long id);
}
