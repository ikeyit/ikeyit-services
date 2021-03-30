package com.ikeyit.passport.service.impl;


import com.ikeyit.passport.service.AuthenticationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            throw new AccessDeniedException("用户没有认证");
        return Long.parseLong(authentication.getName());
    }
}
