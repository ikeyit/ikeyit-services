package com.ikeyit.security.social.weixin;

import com.fasterxml.jackson.databind.JsonNode;
import com.ikeyit.security.social.authentication.AppSocialAuthenticationProvider;
import com.ikeyit.security.social.authentication.AppSocialUserInfo;
import com.ikeyit.security.social.authentication.AppSocialUserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * 移动客户端微信登录认证
 */
public class WeixinAuthenticationProvider extends AppSocialAuthenticationProvider {

    public static final String URL_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={appId}&secret={appSecret}&code={code}&grant_type=authorization_code";

    public static final String URL_USERINFO = "https://api.weixin.qq.com/sns/userinfo?access_token={access_token}&openid=[openid]";

    private RestTemplate restTemplate;

    private WeixinClientRepository clientRepository;

    public WeixinAuthenticationProvider(WeixinClientRepository clientRepository, AppSocialUserService appSocialUserService) {
        super(appSocialUserService);
        this.restTemplate = WeixinRestTemplate.get();
        this.clientRepository = clientRepository;
    }


    @Override
    protected Authentication doAuthenticate(Authentication authentication) throws AuthenticationException {
        WeixinAuthenticationToken authenticationToken = (WeixinAuthenticationToken) authentication;
        String appId = authenticationToken.getAppId();
        String code = authenticationToken.getCode();
        WeixinClient weixinClient = clientRepository.getClient(appId);
        if (weixinClient == null)
            throw new BadCredentialsException("微信客户端未授权，appId：" + appId);

        String appSecret = weixinClient.getAppSecret();
        JsonNode response = null;
        try {
            response = restTemplate.getForObject(URL_ACCESS_TOKEN, JsonNode.class, appId, appSecret, code);
//                {
//                    "access_token": "ACCESS_TOKEN",
//                    "expires_in": 7200,
//                    "refresh_token": "REFRESH_TOKEN",
//                    "openid": "OPENID",
//                    "scope": "SCOPE"
//                }

            int errcode = response.path("errcode").asInt(0);
            if (errcode != 0)
                throw new BadCredentialsException("微信授权验证失败：" + errcode + ", " + response.path("errmsg").asText());

            String access_token = response.get("access_token").asText();
            String openid = response.get("openid").asText();
            response = restTemplate.getForObject(URL_USERINFO, JsonNode.class, access_token, openid);
//                {
//                    "openid": "OPENID",
//                    "nickname": "NICKNAME",
//                    "sex": 1,
//                    "province": "PROVINCE",
//                    "city": "CITY",
//                    "country": "COUNTRY",
//                    "headimgurl": "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
//                    "privilege": ["PRIVILEGE1", "PRIVILEGE2"],
//                    "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
//                }

            errcode = response.path("errcode").asInt();
            if (errcode != 0)
                throw new BadCredentialsException("微信授权验证失败：" + errcode + ", " + response.path("errmsg").asText());
            String avatarUrl = response.get("headimgurl").asText();
            String unionId = response.get("unionid").asText();
            String nickName = response.get("nickname").asText();
            String openId = response.get("openid").asText();

            AppSocialUserInfo appSocialUserInfo = new AppSocialUserInfo();
            appSocialUserInfo.setNick(nickName);
            appSocialUserInfo.setAvatar(avatarUrl);
            appSocialUserInfo.addExtra("appId", weixinClient.getAppId());
            appSocialUserInfo.addExtra("appName", weixinClient.getAppName());

            // union name不为空就启用 union 模式，在union模式下，用unionId而不是openId标识一个微信用户，。
            // 同一个微信用户访问同一个主体下的不同小程序、公众号的openId是不一样的，但unionId是一样的
            if(weixinClient.getUnionName() != null) {
                if (!StringUtils.hasText(unionId))
                    throw new BadCredentialsException("找不到unionid");
                appSocialUserInfo.setProvider("weixin-union-" + weixinClient.getUnionName());
                appSocialUserInfo.setProviderUserId(unionId);
                appSocialUserInfo.addExtra("openId", openId);
                appSocialUserInfo.addExtra("unionName", weixinClient.getUnionName());
            } else {
                appSocialUserInfo.setProvider("weixin-app-" + weixinClient.getAppName());
                appSocialUserInfo.setProviderUserId(openId);
            }

            return new WeixinAuthenticationToken(appSocialUserInfo, null);
        } catch (Exception ex) {
            throw new BadCredentialsException("微信接口故障", ex);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WeixinAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
