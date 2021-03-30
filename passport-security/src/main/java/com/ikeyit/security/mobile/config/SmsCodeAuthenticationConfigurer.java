package com.ikeyit.security.mobile.config;

import com.ikeyit.security.mobile.authentication.*;
import com.ikeyit.security.mobile.web.SmsCodeAuthenticationFilter;
import com.ikeyit.security.mobile.web.SmsCodeSenderFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.Assert;

public class SmsCodeAuthenticationConfigurer <B extends HttpSecurityBuilder<B>>
		extends AbstractHttpConfigurer<SmsCodeAuthenticationConfigurer<B>, B> {

    SmsCodeService smsCodeService;

    MobileUserService mobileUserService;

    private String loginUrl;

    private AuthenticationFailureHandler failureHandler;

    private AuthenticationSuccessHandler successHandler;

    private String sendSmsUrl;

    public SmsCodeAuthenticationConfigurer<B> smsCodeService(SmsCodeService smsCodeService) {
        Assert.notNull(smsCodeService, "SmsCodeService cannot be null");
//        this.getBuilder().setSharedObject(SmsCodeService.class, smsCodeService);
        this.smsCodeService = smsCodeService;
        return this;
    }

    public SmsCodeAuthenticationConfigurer<B> mobileUserService(MobileUserService mobileUserService) {
        Assert.notNull(mobileUserService, "mobileUserService cannot be null");
//        this.getBuilder().setSharedObject(MobileUserService.class, mobileUserService);
        this.mobileUserService = mobileUserService;
        return this;
    }

    public SmsCodeAuthenticationConfigurer<B> loginUrl(String loginUrl) {
        Assert.hasText(loginUrl, "loginUrl cannot be empty");
        this.loginUrl = loginUrl;
        return this;
    }


    public SmsCodeAuthenticationConfigurer<B> sendSmsUrl(String sendSmsUrl) {
        Assert.hasText(sendSmsUrl, "sendSmsUrl cannot be empty");
        this.sendSmsUrl = sendSmsUrl;
        return this;
    }

    private SmsCodeService getSmsCodeService(B builder) {
        if (this.smsCodeService != null)
            return this.smsCodeService;

        this.smsCodeService = new DefaultSmsCodeService();
        return this.smsCodeService;
    }


    private MobileUserService getMobileUserService(B builder) {
        if (this.mobileUserService != null)
            return this.mobileUserService;

        this.mobileUserService = new DefaultMobileUserService();
        return this.mobileUserService;
    }

    public SmsCodeAuthenticationConfigurer<B> successHandler(
            AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.successHandler = authenticationSuccessHandler;
        return this;
    }


    public SmsCodeAuthenticationConfigurer<B> failureHandler(
            AuthenticationFailureHandler authenticationFailureHandler) {
        this.failureHandler = authenticationFailureHandler;
        return this;
    }


    @Override
    public void init(B builder) {
        SmsCodeAuthenticationProvider smsCodeAuthenticationProvider =
                new SmsCodeAuthenticationProvider(getSmsCodeService(builder), getMobileUserService(builder));
        builder.authenticationProvider(smsCodeAuthenticationProvider);
    }


    @Override
    public void configure(B builder) {
//        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

        SmsCodeSenderFilter smsCodeSenderFilter = new SmsCodeSenderFilter(getSmsCodeService(builder), this.sendSmsUrl);
        builder.addFilterBefore(smsCodeSenderFilter, AbstractPreAuthenticatedProcessingFilter.class);

        SmsCodeAuthenticationFilter smsCodeAuthenticationFilter = new SmsCodeAuthenticationFilter(this.loginUrl);
        smsCodeAuthenticationFilter.setAuthenticationManager(builder
                .getSharedObject(AuthenticationManager.class));

        if (this.successHandler != null)
            smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(this.successHandler);
        if (this.failureHandler != null)
            smsCodeAuthenticationFilter.setAuthenticationFailureHandler(this.failureHandler);

        builder.addFilterBefore(smsCodeAuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class);
    }
}
