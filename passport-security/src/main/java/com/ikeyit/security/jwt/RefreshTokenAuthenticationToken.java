package com.ikeyit.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class RefreshTokenAuthenticationToken extends AbstractAuthenticationToken {
    protected final Object principal;

    /**
     * 认证后构造
     * @param principal
     * @param authorities
     */
    public RefreshTokenAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    public RefreshTokenAuthenticationToken(Object principal) {
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
