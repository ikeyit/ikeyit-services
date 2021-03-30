package com.ikeyit.security.social.authentication;

import java.util.HashMap;

public class AppSocialUserInfo {

    private String providerUserId;

    private String provider;

    private String avatar;

    private String nick;

    private HashMap<String, Object> extras = new HashMap<>();

    public AppSocialUserInfo(String providerUserId, String provider, String avatar, String nick) {
        this.providerUserId = providerUserId;
        this.provider = provider;
        this.avatar = avatar;
        this.nick = nick;
    }

    public AppSocialUserInfo(String providerUserId, String provider) {
        this.providerUserId = providerUserId;
        this.provider = provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }


    public String getProvider() {
        return provider;
    }

    public String getNick() {
        return nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void addExtra(String name, Object value) {
        extras.put(name, value);
    }

    public Object getExtra(String name) {
        return extras.get(name);
    }
}
