package com.ikeyit.security.social.config;

import com.ikeyit.security.social.authentication.AppSocialUserService;
import com.ikeyit.security.social.authentication.DefaultAppSocialUserService;
import com.ikeyit.security.social.web.WeixinAuthenticationFilter;
import com.ikeyit.security.social.weixin.InMemoryWeixinClientRepository;
import com.ikeyit.security.social.weixin.MiniProgramAuthenticationProvider;
import com.ikeyit.security.social.weixin.WeixinAuthenticationProvider;
import com.ikeyit.security.social.weixin.WeixinClientRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.Assert;

public class WeixinAuthenticationConfigurer<B extends HttpSecurityBuilder<B>>
        extends AbstractHttpConfigurer<WeixinAuthenticationConfigurer<B>, B> {

    private String loginUrl;

    private AuthenticationFailureHandler failureHandler;

    private AuthenticationSuccessHandler successHandler;

    private AppSocialUserService appSocialUserService;

    private WeixinClientRepository weixinClientRepository;

    public WeixinAuthenticationConfigurer<B> weixinClientRepository(WeixinClientRepository weixinClientRepository) {
        Assert.notNull(weixinClientRepository, "WeixinClientRepository cannot be null");
        this.weixinClientRepository = weixinClientRepository;
        return this;
    }

    public WeixinAuthenticationConfigurer<B> appSocialUserService(AppSocialUserService appSocialUserService) {
        Assert.notNull(appSocialUserService, "appSocialUserService cannot be null");
//        this.getBuilder().setSharedObject(MobileUserService.class, mobileUserService);
        this.appSocialUserService = appSocialUserService;
        return this;
    }


    public WeixinAuthenticationConfigurer<B> loginUrl(String loginUrl) {
        Assert.hasText(loginUrl, "loginUrl cannot be empty");
        this.loginUrl = loginUrl;
        return this;
    }


    public WeixinAuthenticationConfigurer<B> successHandler(
            AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.successHandler = authenticationSuccessHandler;
        return this;
    }


    public WeixinAuthenticationConfigurer<B> failureHandler(
            AuthenticationFailureHandler authenticationFailureHandler) {
        this.failureHandler = authenticationFailureHandler;
        return this;
    }


    private WeixinClientRepository getWeixinClientRepository(B builder) {
//        WeixinClientRepository weixinClientRepository = builder.getSharedObject(WeixinClientRepository.class);
//        if (weixinClientRepository == null)
//            weixinClientRepository = builder.getSharedObject(ApplicationContext.class).getBean(WeixinClientRepository.class);
//        if (weixinClientRepository == null) {
//            weixinClientRepository = new InMemoryWeixinClientRepository();
//            builder.setSharedObject(WeixinClientRepository.class, weixinClientRepository);
//        }
//
//        return weixinClientRepository;


        if (this.weixinClientRepository != null)
            return this.weixinClientRepository;

        this.weixinClientRepository = new InMemoryWeixinClientRepository();
        return this.weixinClientRepository;
    }


    private AppSocialUserService appSocialUserService(B builder) {
        if (this.appSocialUserService != null)
            return this.appSocialUserService;

        this.appSocialUserService = new DefaultAppSocialUserService();
        return this.appSocialUserService;
    }

    @Override
    public void init(B builder) {
        WeixinAuthenticationProvider weixinAuthenticationProvider =
                new WeixinAuthenticationProvider(getWeixinClientRepository(builder), appSocialUserService(builder));
        builder.authenticationProvider(weixinAuthenticationProvider);

        MiniProgramAuthenticationProvider miniProgramAuthenticationProvider =
                new MiniProgramAuthenticationProvider(getWeixinClientRepository(builder), appSocialUserService(builder));
        builder.authenticationProvider(miniProgramAuthenticationProvider);
    }


    @Override
    public void configure(B builder) {
        WeixinAuthenticationFilter weixinAuthenticationFilter = new WeixinAuthenticationFilter(this.loginUrl);
        weixinAuthenticationFilter.setAuthenticationManager(builder
                .getSharedObject(AuthenticationManager.class));

        if (this.successHandler != null)
            weixinAuthenticationFilter.setAuthenticationSuccessHandler(this.successHandler);
        if (this.failureHandler != null)
            weixinAuthenticationFilter.setAuthenticationFailureHandler(this.failureHandler);
        builder.addFilterBefore(weixinAuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class);
    }
}
