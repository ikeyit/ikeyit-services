package com.ikeyit.user.controller;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.user.domain.Address;
import com.ikeyit.user.domain.UserDetail;
import com.ikeyit.user.service.AddressService;
import com.ikeyit.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @GetMapping("/user")
    public UserDetail getUser() {
        return userService.getCurrentUser();
    }


    @GetMapping("/user/{id}")
    public UserDetail getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/users")
    public Page<UserDetail> getUser(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "3") int pageSize) {
        return userService.getUsers(new PageParam(page, pageSize));
    }


    @PatchMapping("/user")
    public UserDetail updateUser(
                           @RequestParam(required = false) String nick,
                           @RequestParam(required = false) String avatar,
                           @RequestParam(required = false) String location,
                           @RequestParam(required = false) Integer sex) {
        return userService.updateUser(nick, avatar, location, sex);
    }

    @GetMapping("/user/addresses")
    public List<Address> getUserAddresses() {

        return addressService.getAddresses();
    }

    @GetMapping("/user/address")
    public Address getUserDefaultAddress() {
        return addressService.getDefaultAddress();
    }

    @PostMapping("/user/address")
    public Address addUserAddress(Address address) {
        return addressService.addAddress(address);
    }

    @GetMapping("/user/address/{id}")
    public Address getUserAddress(@PathVariable Long id) {
        return addressService.getUserAddress(id);
    }


    @PutMapping("/user/address/{id}")
    public Address updateUserAddress(@PathVariable Long id, Address address) {
        address.setId(id);
        return addressService.updateAddress(address);
    }

    @DeleteMapping("/user/address/{id}")
    public void deleteUserAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
    }


}
