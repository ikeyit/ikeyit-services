package com.ikeyit.security.social.web;

import com.ikeyit.security.social.authentication.AppSocialAuthenticationToken;
import com.ikeyit.security.social.weixin.MiniProgramAuthenticationToken;
import com.ikeyit.security.social.weixin.WeixinAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 手机客户端/小程序等第三方登录验证
 * 客户端通过第三方lib获得openid和access_token后发送到服务器这里，进行验证，绑定本地用户
 *
 */
public class WeixinAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String DEFAULT_ENDPOINT_URI = "/auth/weixin";

    public WeixinAuthenticationFilter(String endpointUri) {
        this(new AntPathRequestMatcher(endpointUri == null ? DEFAULT_ENDPOINT_URI : endpointUri));
    }

    public WeixinAuthenticationFilter(RequestMatcher endpointMatcher) {
        super(endpointMatcher);
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String appId = request.getParameter("appId");
        String code = request.getParameter("code");
        String userInfo = request.getParameter("userInfo");
        String iv = request.getParameter("iv");
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(code))
            throw new BadCredentialsException("认证参数缺失");

        Authentication authRequest = null;

        if (StringUtils.hasText(userInfo) && StringUtils.hasText(iv)) {
            //小程序
            authRequest = new MiniProgramAuthenticationToken(appId, code, userInfo, iv);
        } else {
            authRequest = new WeixinAuthenticationToken(appId, code);
        }

        AppSocialAuthenticationToken authenticationResult = (AppSocialAuthenticationToken)getAuthenticationManager().authenticate(authRequest);
        return authenticationResult;
    }
}