package com.ikeyit.passport.service;

import com.ikeyit.passport.domain.WeixinConnection;
import com.ikeyit.passport.repository.WeixinConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConnectionService {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    WeixinConnectionRepository weixinConnectionRepository;

    public WeixinConnection getWeixinConnection(String appId) {
        Long userId = authenticationService.getCurrentUserId();
        return weixinConnectionRepository.getByUserAndAppId(userId, appId);
    }
}
