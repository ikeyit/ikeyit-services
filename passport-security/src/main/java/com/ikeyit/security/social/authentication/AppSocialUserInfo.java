package com.ikeyit.security.social.authentication;

import java.util.HashMap;

public class AppSocialUserInfo {

    private String providerUserId;

    private String provider;

    private String avatar;

    private String nick;

    private HashMap<String, Object> extras = new HashMap<>();

    public AppSocialUserInfo() {
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void addExtra(String name, Object value) {
        extras.put(name, value);
    }

    public Object getExtra(String name) {
        return extras.get(name);
    }


}
