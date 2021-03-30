package com.ikeyit.security.social.weixin;

import org.springframework.util.Assert;

import java.util.HashMap;

public class InMemoryWeixinClientRepository implements WeixinClientRepository {

    private HashMap<String, WeixinClient> clients = new HashMap<>();

    public InMemoryWeixinClientRepository() {
    }


    @Override
    public WeixinClient getClient(String appId) {
        return clients.get(appId);
    }


    public InMemoryWeixinClientRepository addClient(WeixinClient client) {
        Assert.notNull(client,"client should be not null");
        clients.put(client.getAppId(), client);
        return this;
    }

    public InMemoryWeixinClientRepository addClient(String appId, String appSecret, String appName) {
        Assert.notNull(appId,"client should be not null");
        Assert.notNull(appSecret,"appSecret should be not null");
        addClient(new WeixinClient(appId, appSecret, appName));
        return this;
    }
}
