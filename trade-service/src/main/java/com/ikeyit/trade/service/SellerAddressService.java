package com.ikeyit.trade.service;


import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.trade.domain.SellerAddress;
import com.ikeyit.trade.repository.SellerAddressRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SellerAddressService {

    @Autowired
    SellerAddressRepository addressRepository;

    @Autowired
    AuthenticationService authenticationService;

    public List<SellerAddress> getAddresses() {
        Long userId = authenticationService.getCurrentUserId();
        return addressRepository.getBySellerId(userId);
    }

    public SellerAddress getDefaultShipFromAddress(Long sellerId) {
        return addressRepository.getDefaultShipFrom(sellerId);
    }

    public SellerAddress getDefaultReturnToAddress(Long sellerId) {
        return addressRepository.getDefaultReturnTo(sellerId);
    }

    public SellerAddress getDefaultShipFromAddress() {
        Long userId = authenticationService.getCurrentUserId();
        return addressRepository.getDefaultShipFrom(userId);
    }

    public SellerAddress getDefaultReturnToAddress() {
        Long userId = authenticationService.getCurrentUserId();
        return addressRepository.getDefaultReturnTo(userId);
    }

    public SellerAddress getAddress(Long id) {
        SellerAddress address = addressRepository.getById(id);
        Long userId = authenticationService.getCurrentUserId();
        if (address == null)
            throw new IllegalArgumentException("地址不存在");
        if (!address.getSellerId().equals(userId))
            throw new IllegalArgumentException("地址访问非法");
        return address;
    }


    @Transactional
    public SellerAddress addAddress(SellerAddress address) {
        validateAddress(address);
        Long userId = authenticationService.getCurrentUserId();
        address.setSellerId(userId);
        if (Boolean.TRUE.equals(address.getDefaultShipFrom()))
            addressRepository.clearDefaultShipFrom(userId);
        if (Boolean.TRUE.equals(address.getDefaultReturnTo()))
            addressRepository.clearDefaultReturnTo(userId);

        addressRepository.create(address);
        return address;
    }


    @Transactional
    public int updateAddress(SellerAddress address) {
        validateAddress(address);
        SellerAddress oldAddress = getAddress(address.getId());
        if (Boolean.TRUE.equals(address.getDefaultShipFrom())
                && Boolean.FALSE.equals(oldAddress.getDefaultShipFrom()) )
            addressRepository.clearDefaultShipFrom(oldAddress.getSellerId());
        if (Boolean.TRUE.equals(address.getDefaultReturnTo())
                && Boolean.FALSE.equals(oldAddress.getDefaultReturnTo()) )
            addressRepository.clearDefaultReturnTo(oldAddress.getSellerId());
        return addressRepository.update(address);
    }


    private void validateAddress(SellerAddress address) {
        if (StringUtils.isAnyBlank(
                address.getName(),
                address.getPhone(),
                address.getProvince(),
                address.getCity(),
                address.getDistrict(),
                address.getStreet()))
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);

        if (address.getDefaultShipFrom() == null )
            address.setDefaultShipFrom(Boolean.FALSE);
        if (address.getDefaultReturnTo() == null )
            address.setDefaultReturnTo(Boolean.FALSE);
    }

    public void deleteAddress(Long addressId) {
        getAddress(addressId);
        addressRepository.delete(addressId);
    }
}
