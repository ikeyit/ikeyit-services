package com.ikeyit.security.mobile.web;

import com.ikeyit.security.mobile.authentication.SmsCodeAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String DEFAULT_ENDPOINT_URI = "/auth/smscode";

    public SmsCodeAuthenticationFilter() {
        this(DEFAULT_ENDPOINT_URI);
    }

    public SmsCodeAuthenticationFilter(String endpointUri) {
        super(endpointUri == null ? DEFAULT_ENDPOINT_URI : endpointUri);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String mobile = request.getParameter("mobile");
        String code = request.getParameter("code");
        Authentication authRequest = new SmsCodeAuthenticationToken(mobile, code);
        if (!StringUtils.hasText(mobile) || !StringUtils.hasText(code))
            throw new BadCredentialsException("手机号或验证码不能为空");

       return getAuthenticationManager().authenticate(authRequest);
    }
}





