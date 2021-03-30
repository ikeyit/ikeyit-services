package com.ikeyit.security.social.weixin;

public class WeixinClient {

    private String appId;

    private String appSecret;

    private String appName;

    private String unionName;

    public WeixinClient() {

    }

    public WeixinClient(String appId, String appSecret, String appName) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.appName = appName;
    }


    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUnionName() {
        return unionName;
    }

    public void setUnionName(String unionName) {
        this.unionName = unionName;
    }
}
