package com.ikeyit.security.mobile.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikeyit.security.common.RestError;
import com.ikeyit.security.mobile.authentication.SmsCodeException;
import com.ikeyit.security.mobile.authentication.SmsCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SmsCodeSenderFilter extends OncePerRequestFilter {

    public static final String DEFAULT_ENDPOINT_URI = "/auth/smscode/code";

    private RequestMatcher endpointMatcher;

    private SmsCodeService smsCodeService;

    private ObjectMapper objectMapper = new ObjectMapper();

    public SmsCodeSenderFilter(SmsCodeService smsCodeService) {
        this(smsCodeService, DEFAULT_ENDPOINT_URI);
    }

    public SmsCodeSenderFilter(SmsCodeService smsCodeService, String endpointUri) {
        this.smsCodeService = smsCodeService;
        if (endpointUri == null)
            endpointUri = DEFAULT_ENDPOINT_URI;
        this.endpointMatcher = new AntPathRequestMatcher(endpointUri);;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!this.endpointMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String mobile = request.getParameter("mobile");
        try {
            smsCodeService.sendCode(mobile);
        } catch (SmsCodeException e) {
            failureHandler(response, e);
        }
    }

    private void failureHandler(HttpServletResponse response, SmsCodeException ex) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), new RestError("FAIL_SEND_SMSCODE", ex.getMessage()));
    }
}
