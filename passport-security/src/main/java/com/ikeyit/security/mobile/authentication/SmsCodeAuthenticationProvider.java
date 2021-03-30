package com.ikeyit.security.mobile.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 短信验证码认证provider
 *
 */
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {

    private MobileUserService smsCodeUserService;

    private SmsCodeService smsCodeService;

    public SmsCodeAuthenticationProvider(SmsCodeService smsCodeService, MobileUserService smsCodeUserService) {
        this.smsCodeUserService = smsCodeUserService;
        this.smsCodeService = smsCodeService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
            String mobile = (String) authenticationToken.getPrincipal();
            String code = (String) authenticationToken.getCredentials();
            smsCodeService.validate(mobile, code);
            UserDetails userDetails = smsCodeUserService.loadUserByMobile(mobile);
            if (userDetails == null)
                throw new BadCredentialsException("用户不存在");

            //鉴权成功，返回一个新的SmsCodeAuthenticationToken 此时pricipal为UserDetails
            SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(userDetails, userDetails.getAuthorities());
            authenticationResult.setDetails(authenticationToken.getDetails());
            return authenticationResult;
        } catch (SmsCodeException e) {
            throw new BadCredentialsException(e.getLocalizedMessage());
        }
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
