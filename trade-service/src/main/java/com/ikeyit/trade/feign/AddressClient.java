package com.ikeyit.trade.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "passport-service", contextId="AddressClient")
public interface AddressClient {

    @GetMapping("/user/addresses")
    List<AddressDTO> getUserAddresses();

    @GetMapping("/user/address")
    AddressDTO getUserDefaultAddress();

    @GetMapping("/user/address/{id}")
    AddressDTO getUserAddress(@PathVariable Long id);

}
