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
        authenticationService.requireAuthority("r_super");
        weixinClientRepository.create(weixinClient);
    }

    public void updateWeixinClient(WeixinClient weixinClient) {
        authenticationService.requireAuthority("r_super");
        weixinClientRepository.update(weixinClient);
    }

    public void deleteWeixinClient(String appId) {
        authenticationService.requireAuthority("r_super");
        weixinClientRepository.deleteByAppId(appId);
    }

    public List<WeixinClient> getWeixinClients() {
        authenticationService.requireAuthority("r_super");
        return weixinClientRepository.list();
    }
}
