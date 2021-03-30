package com.ikeyit.security.jwt;

import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RefreshTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String DEFAULT_ENDPOINT_URI = "/auth/refresh_token";

    public RefreshTokenAuthenticationFilter() {
        this(DEFAULT_ENDPOINT_URI);
    }

    public RefreshTokenAuthenticationFilter(String endpointUri) {
        super(new AntPathRequestMatcher(endpointUri == null ? DEFAULT_ENDPOINT_URI : endpointUri, HttpMethod.POST.name()));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String refreshToken = request.getParameter("token");
        RefreshTokenAuthenticationToken refreshTokenAuthenticationToken = new RefreshTokenAuthenticationToken(refreshToken);
       return getAuthenticationManager().authenticate(refreshTokenAuthenticationToken);
    }
}





