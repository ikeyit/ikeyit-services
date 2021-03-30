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
        WeixinClient miniProgramClient = clientRepository.getClient(appId);
        if (miniProgramClient == null)
            throw new BadCredentialsException("未找到小程序客户端");

        String appSecret = miniProgramClient.getAppSecret();
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

            if(miniProgramClient.getUnionName() != null) {
                if (!StringUtils.hasText(unionId))
                    throw new BadCredentialsException("找不到unionid");

                AppSocialUserInfo appSocialUserInfo = new AppSocialUserInfo(unionId, miniProgramClient.getUnionName(), avatarUrl, nickName);
                appSocialUserInfo.addExtra("openId", openId);
                appSocialUserInfo.addExtra("appName", miniProgramClient.getAppName());
                return new WeixinAuthenticationToken(appSocialUserInfo, null);
            } else
                return new WeixinAuthenticationToken(new AppSocialUserInfo(openid, miniProgramClient.getAppName(), avatarUrl, nickName),null);

        } catch (Exception ex) {
            throw new BadCredentialsException("微信接口故障", ex);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WeixinAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
