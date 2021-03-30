package com.ikeyit.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;

public interface RefreshTokenUserService {
    UserDetails loadUserBySubject(String subject);
}
