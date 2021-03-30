package com.ikeyit.security.social.weixin;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class MiniProgramAuthenticationToken extends AbstractAuthenticationToken {

    private Object principal;

    private String code;

    private String userInfo;

    private String iv;

    public MiniProgramAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
    }


    public MiniProgramAuthenticationToken(String appId, String code, String userInfo, String iv) {
        super(null);
        this.principal = appId;
        this.code = code;
        this.userInfo = userInfo;
        this.iv = iv;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public String getAppId() {
        return this.principal.toString();
    }

    public String getCode() {
        return code;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public String getIv() {
        return iv;
    }
}
