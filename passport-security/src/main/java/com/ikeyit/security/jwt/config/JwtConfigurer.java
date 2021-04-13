package com.ikeyit.security.jwt.config;

import com.ikeyit.security.jwt.*;
import com.ikeyit.security.mobile.config.SmsCodeAuthenticationConfigurer;
import com.ikeyit.security.social.config.WeixinAuthenticationConfigurer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.Assert;

public class JwtConfigurer<B extends HttpSecurityBuilder<B>>
        extends AbstractHttpConfigurer<JwtConfigurer<B>, B> {

    private JwtService jwtService;

    private RefreshTokenUserService refreshTokenUserService;

    private String jwksUrl;

    private String refreshTokenUrl;

    private JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler;

    private JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

    public JwtConfigurer<B> jwtService(JwtService jwtService) {
        Assert.notNull(jwtService, "jwtService cannot be null");
        this.jwtService = jwtService;
        return this;
    }

    public JwtConfigurer<B> refreshTokenUserService(RefreshTokenUserService refreshTokenUserService) {
        Assert.notNull(refreshTokenUserService, "refreshTokenUserService cannot be null");
        this.refreshTokenUserService = refreshTokenUserService;
        return this;
    }

    public JwtConfigurer<B> jwksUrl(String jwksUrl) {
        Assert.hasText(jwksUrl, "jwksUrl cannot be empty");
        this.jwksUrl = jwksUrl;
        return this;
    }


    public JwtConfigurer<B> refreshTokenUrl(String refreshTokenUrl) {
        Assert.hasText(refreshTokenUrl, "refreshTokenUrl cannot be empty");
        this.refreshTokenUrl = refreshTokenUrl;
        return this;
    }
    public JwtConfigurer<B>  jwtAuthenticationFailureHandler(JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler) {
        this.jwtAuthenticationFailureHandler = jwtAuthenticationFailureHandler;
        return this;
    }


    private JwtService getJwtService(B builder) {
        if (this.jwtService != null)
            return this.jwtService;
        this.jwtService = new JwtService();
        return this.jwtService;
    }

    private RefreshTokenUserService getRefreshTokenUserService(B builder) {
        if (this.refreshTokenUserService != null)
            return this.refreshTokenUserService;

        UserDetailsService userDetailsService = builder.getSharedObject(UserDetailsService.class);
        if (userDetailsService == null)
            userDetailsService = new InMemoryUserDetailsManager();

        this.refreshTokenUserService = new DefaultRefreshTokenUserService(userDetailsService);
        return this.refreshTokenUserService;
    }


    @Override
    public void init(B builder) {
        if (jwtAuthenticationFailureHandler == null)
            jwtAuthenticationFailureHandler = new JwtAuthenticationFailureHandler();
        jwtAuthenticationSuccessHandler = new JwtAuthenticationSuccessHandler(getJwtService(builder));
        FormLoginConfigurer<B> formLoginConfigurer = getBuilder().getConfigurer(
                FormLoginConfigurer.class);
        if (formLoginConfigurer != null) {
            formLoginConfigurer
                    .successHandler(jwtAuthenticationSuccessHandler)
                    .failureHandler(jwtAuthenticationFailureHandler);
        }


        RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider =
                new RefreshTokenAuthenticationProvider(getJwtService(builder), getRefreshTokenUserService(builder));
        builder.authenticationProvider(refreshTokenAuthenticationProvider);

        SmsCodeAuthenticationConfigurer<B> smsCodeAuthenticationConfigurer = getBuilder().getConfigurer(
                SmsCodeAuthenticationConfigurer.class);

        if (smsCodeAuthenticationConfigurer != null) {
            smsCodeAuthenticationConfigurer
                    .successHandler(jwtAuthenticationSuccessHandler)
                    .failureHandler(jwtAuthenticationFailureHandler);
        }

        WeixinAuthenticationConfigurer<B> weixinAuthenticationConfigurer = getBuilder().getConfigurer(WeixinAuthenticationConfigurer.class);

        if (weixinAuthenticationConfigurer != null)
            weixinAuthenticationConfigurer
                    .successHandler(jwtAuthenticationSuccessHandler)
                    .failureHandler(jwtAuthenticationFailureHandler);

        RestAuthenticationEntryPoint restAuthenticationEntryPoint = new RestAuthenticationEntryPoint();
        RestAccessDeniedHandler restAccessDeniedHandler = new RestAccessDeniedHandler();
        ExceptionHandlingConfigurer<B> exceptionHandlingConfigurer = getBuilder().getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandlingConfigurer != null) {
            exceptionHandlingConfigurer
                    .authenticationEntryPoint(restAuthenticationEntryPoint)
                    .accessDeniedHandler(restAccessDeniedHandler);
        }

        OAuth2ResourceServerConfigurer<B> oAuth2ResourceServerConfigurer = getBuilder().getConfigurer(OAuth2ResourceServerConfigurer.class);
        if (oAuth2ResourceServerConfigurer != null) {
            oAuth2ResourceServerConfigurer
                    .authenticationEntryPoint(restAuthenticationEntryPoint)
                    .accessDeniedHandler(restAccessDeniedHandler);
        }
    }


    @Override
    public void configure(B builder) {
        JwkSetEndpointFilter jwkSetEndpointFilter = new JwkSetEndpointFilter(getJwtService(builder), this.jwksUrl);
        builder.addFilterBefore(jwkSetEndpointFilter, AbstractPreAuthenticatedProcessingFilter.class);
        RefreshTokenAuthenticationFilter refreshTokenAuthenticationFilter = new RefreshTokenAuthenticationFilter(this.refreshTokenUrl);
        refreshTokenAuthenticationFilter.setAuthenticationManager(builder.getSharedObject(AuthenticationManager.class));
        refreshTokenAuthenticationFilter.setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
        refreshTokenAuthenticationFilter.setAuthenticationFailureHandler(jwtAuthenticationFailureHandler);
        builder.addFilterBefore(refreshTokenAuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class);
    }
}