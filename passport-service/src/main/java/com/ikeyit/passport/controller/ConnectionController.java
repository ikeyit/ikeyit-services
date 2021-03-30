package com.ikeyit.passport.controller;

import com.ikeyit.passport.domain.WeixinConnection;
import com.ikeyit.passport.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConnectionController {
    @Autowired
    ConnectionService connectionService;

    @GetMapping("/connection/weixin/{appId}")
    public WeixinConnection getWeixinConnection(@PathVariable String appId) {
        return connectionService.getWeixinConnection(appId);
    }
}
