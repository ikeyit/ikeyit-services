package com.ikeyit.user.service;

import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.user.domain.Address;
import com.ikeyit.user.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {


    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    AddressRepository addressRepository;


    public List<Address> getAddresses() {
        Long userId = authenticationService.getCurrentUserId();
        return addressRepository.getByUserId(userId);
    }


    public Address getDefaultAddress() {
        Long userId = authenticationService.getCurrentUserId();
        Address address = addressRepository.getPreferredByUserId(userId);
        if (address == null)
           return addressRepository.getLatestByUserId(userId);
        return address;
    }


    public Address getUserAddress(Long id) {
        Address address = addressRepository.getById(id);
        Long userId = authenticationService.getCurrentUserId();
        if (address == null)
            throw new IllegalArgumentException("地址不存在");
        if (!address.getUserId().equals(userId))
            throw new IllegalArgumentException("地址访问非法");
        return address;
    }


    @Transactional
    public Address addAddress(Address address) {
        Long userId = authenticationService.getCurrentUserId();
        address.setUserId(userId);
        if (null == address.getPreferred())
            address.setPreferred(Boolean.FALSE);
        if (Boolean.TRUE.equals(address.getPreferred()))
            addressRepository.clearPreferred(userId);
        addressRepository.create(address);
        return address;
    }


    @Transactional
    public Address updateAddress(Address newAddress) {
        Address address = getUserAddress(newAddress.getId());
        if (newAddress.getPreferred() && !address.getPreferred())
            addressRepository.clearPreferred(address.getUserId());

        newAddress.setUserId(address.getUserId());
        addressRepository.update(newAddress);
        return newAddress;
    }

    public void deleteAddress(Long addressId) {
        Long userId = authenticationService.getCurrentUserId();
        Address address = addressRepository.getById(addressId);
        if (address == null)
            throw new IllegalArgumentException("要删除的地址不存在");

        if (!address.getUserId().equals(userId))
            throw new IllegalArgumentException("不能删除他人的地址");

        addressRepository.delete(addressId);
    }

}
