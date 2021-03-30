package com.ikeyit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikeyit.security.common.RestError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 当发现用户没有认证后，Rest并不用重定向到登录form。
 * 直接返回禁止访问，原因为没有认证
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), new RestError("UNAUTHORIZED", "禁止访问，请先认证", authException.getMessage()));
    }
}

