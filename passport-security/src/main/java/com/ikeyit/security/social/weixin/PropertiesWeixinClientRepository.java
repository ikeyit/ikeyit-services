package com.ikeyit.security.social.weixin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "weixin.oauth")
public class PropertiesWeixinClientRepository implements WeixinClientRepository  {

    private HashMap<String, WeixinClient> clientsMap = new HashMap<>();

    private List<WeixinClient> clients;

    public void setClients(List<WeixinClient> clients) {
        for (WeixinClient client: clients) {
            clientsMap.put(client.getAppId(), client);
        }
    }

    public List<WeixinClient> getClients() {
        return clients;
    }

    @Override
    public WeixinClient getClient(String appId) {
        return clientsMap.get(appId);
    }
}
