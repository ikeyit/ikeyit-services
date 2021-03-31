package com.ikeyit.passport.controller;

import com.ikeyit.passport.service.WeixinClientService;
import com.ikeyit.security.social.weixin.WeixinClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WeixinClientController {

    @Autowired
    WeixinClientService weixinClientService;

    @GetMapping("/weixin_clients")
    public List<WeixinClient> getWeixinClients() {
        return weixinClientService.getWeixinClients();
    }

    @PostMapping("/weixin_client")
    public void createWeixinClient(@RequestBody WeixinClient weixinClient) {
        weixinClientService.createWeixinClient(weixinClient);
    }

    @PutMapping("/weixin_client/{appId}")
    public void updateWeixinClient(@PathVariable String appId, @RequestBody WeixinClient weixinClient) {
        weixinClient.setAppId(appId);
        weixinClientService.updateWeixinClient(weixinClient);
    }

    @DeleteMapping("/weixin_client/{appId}")
    public void deleteWeixinClient(@PathVariable String appId) {
        weixinClientService.deleteWeixinClient(appId);
    }
}
