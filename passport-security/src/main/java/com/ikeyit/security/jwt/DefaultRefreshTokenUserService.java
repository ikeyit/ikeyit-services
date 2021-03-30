package com.ikeyit.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class DefaultRefreshTokenUserService implements RefreshTokenUserService {

    private UserDetailsService delegate = null;

    public DefaultRefreshTokenUserService(UserDetailsService delegate) {
        this.delegate = delegate;
    }

    @Override
    public UserDetails loadUserBySubject(String subject) {
        return delegate.loadUserByUsername(subject);
    }
}
