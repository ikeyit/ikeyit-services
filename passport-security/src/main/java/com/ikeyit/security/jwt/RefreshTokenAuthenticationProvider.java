package com.ikeyit.security.jwt;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 短信验证码认证provider
 *
 */
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {

    private JwtService jwtService;

    private RefreshTokenUserService refreshTokenUserService;

    public RefreshTokenAuthenticationProvider(JwtService jwtService, RefreshTokenUserService refreshTokenUserService) {
        this.jwtService = jwtService;
        this.refreshTokenUserService = refreshTokenUserService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RefreshTokenAuthenticationToken authenticationToken = (RefreshTokenAuthenticationToken) authentication;
        String token = (String) authenticationToken.getPrincipal();
        if (token == null)
            throw new BadCredentialsException("refresh token 不存在！");
        RefreshToken refreshToken = jwtService.decodeRefreshToken(token);
        UserDetails userDetails = refreshTokenUserService.loadUserBySubject(refreshToken.getSubject());
        if (userDetails == null)
            throw new BadCredentialsException("用户不存在");
        RefreshTokenAuthenticationToken authenticationResult = new RefreshTokenAuthenticationToken(userDetails, userDetails.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return RefreshTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
