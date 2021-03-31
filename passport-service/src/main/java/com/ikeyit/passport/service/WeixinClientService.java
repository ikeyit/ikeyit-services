package com.ikeyit.passport.service;

import com.ikeyit.passport.repository.DbWeixinClientRepository;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.security.social.weixin.WeixinClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeixinClientService {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    DbWeixinClientRepository weixinClientRepository;


    public void createWeixinClient(WeixinClient weixinClient) {
        authenticationService.requireAuthority("ROLE_SUPER");
        weixinClientRepository.create(weixinClient);
    }

    public void updateWeixinClient(WeixinClient weixinClient) {
        authenticationService.requireAuthority("ROLE_SUPER");
        weixinClientRepository.update(weixinClient);
    }

    public void deleteWeixinClient(String appId) {
        authenticationService.requireAuthority("ROLE_SUPER");
        weixinClientRepository.deleteByAppId(appId);
    }

    public List<WeixinClient> getWeixinClients() {
        authenticationService.requireAuthority("ROLE_SUPER");
        return weixinClientRepository.list();
    }
}
