package com.ikeyit.pay.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passport-service", contextId="ConnectionClient", configuration = FeignConfig.class)
public interface ConnectionClient {
    @GetMapping("/connection/weixin/{appId}")
    WeixinConnectionDTO getWeixinConnection(@PathVariable String appId);
}
