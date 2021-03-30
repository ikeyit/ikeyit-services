package com.ikeyit.security.jwt;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwkSetEndpointFilter extends OncePerRequestFilter {
    private static final String DEFAULT_JWK_SET_URI = "/.well-known/jwks.json";
    private RequestMatcher requestMatcher;
    private JwtService jwtService;

    public JwkSetEndpointFilter(JwtService jwtService) {
        this(jwtService,null);
    }

    public JwkSetEndpointFilter(JwtService jwtService, String url) {
        Assert.notNull(jwtService, "jwtService cannot be null");
        this.requestMatcher = new AntPathRequestMatcher(url == null ? DEFAULT_JWK_SET_URI : url, HttpMethod.GET.name());
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (requestMatcher.matches(request)) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(jwtService.getJwkSet().toJSONObject().toJSONString());
        } else {
            filterChain.doFilter(request, response);
        }
    }
}