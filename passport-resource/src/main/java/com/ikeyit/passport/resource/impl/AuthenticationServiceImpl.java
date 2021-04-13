package com.ikeyit.passport.resource.impl;


import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.passport.resource.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * 使用spring security作为后端基础设施
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        try {
            return Long.parseLong(authentication.getName());
        } catch (Exception e) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }
    }

    @Override
    public Long pollCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return null;
        try {
            return Long.parseLong(authentication.getName());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return false;
        return hasAuthority(authentication, authority);
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        if (authority == null)
            return false;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null)
            return false;

        for (GrantedAuthority grantedAuthority : authorities) {
            if (authority.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void requireAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        if (!hasAuthority(authentication, authority))
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
    }
}
