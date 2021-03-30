package com.ikeyit.security.social.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 第三方登录认证
 */
public abstract class AppSocialAuthenticationProvider implements AuthenticationProvider {
    AppSocialUserService appSocialUserService;

    public AppSocialAuthenticationProvider(AppSocialUserService appSocialUserService) {
        this.appSocialUserService = appSocialUserService;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authentication auth = doAuthenticate(authentication);
        AppSocialUserInfo socialUserInfo = (AppSocialUserInfo) auth.getPrincipal();

        UserDetails userPrincipal = appSocialUserService.loadUserBySocialUser(socialUserInfo);
        AppSocialAuthenticationToken authenticationResult = new AppSocialAuthenticationToken(
                userPrincipal,
                userPrincipal.getAuthorities());
        authenticationResult.setDetails(auth.getDetails());
        return authenticationResult;
    }

    protected abstract Authentication doAuthenticate(Authentication authentication);

}
