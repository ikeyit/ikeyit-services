package com.ikeyit.security.social.weixin;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class WeixinAuthenticationToken extends AbstractAuthenticationToken {

    private Object principal;

    private String code;

    public WeixinAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
    }

    public WeixinAuthenticationToken(String appId, String code) {
        super(null);
        this.principal = appId;
        this.code = code;
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
}
