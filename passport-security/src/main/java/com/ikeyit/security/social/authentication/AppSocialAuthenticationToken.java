package com.ikeyit.security.social.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AppSocialAuthenticationToken extends AbstractAuthenticationToken {

    protected final Object principal; // appId

    /**
     * 认证后构造
     * @param principal
     * @param authorities
     */
    public AppSocialAuthenticationToken(Object principal,Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    public AppSocialAuthenticationToken(Object principal) {
        super(null);
        this.principal = principal;
    }


    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
