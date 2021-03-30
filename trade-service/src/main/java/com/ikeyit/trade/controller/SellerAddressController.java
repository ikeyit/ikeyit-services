package com.ikeyit.trade.controller;

import com.ikeyit.trade.domain.SellerAddress;
import com.ikeyit.trade.service.SellerAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seller")
public class SellerAddressController {

    @Autowired
    SellerAddressService addressService;

    @GetMapping("/addresses")
    public List<SellerAddress> getAddresses() {
        return addressService.getAddresses();
    }

    @GetMapping("/address/default_return_to")
    public SellerAddress getDefaultReturnToAddress() {
        return addressService.getDefaultReturnToAddress();
    }

    @GetMapping("/address/default_ship_from")
    public SellerAddress getDefaultShipFromAddress() {
        return addressService.getDefaultShipFromAddress();
    }


    @PostMapping("/address")
    public SellerAddress addAddress(SellerAddress address) {
        return addressService.addAddress(address);
    }

    @GetMapping("/address/{id}")
    public SellerAddress getAddress(@PathVariable Long id) {
        return addressService.getAddress(id);
    }


    @PutMapping("/address/{id}")
    public int updateAddress(@PathVariable Long id, SellerAddress address) {
        address.setId(id);
        return addressService.updateAddress(address);
    }

    @DeleteMapping("/address/{id}")
    public void deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
    }

}
