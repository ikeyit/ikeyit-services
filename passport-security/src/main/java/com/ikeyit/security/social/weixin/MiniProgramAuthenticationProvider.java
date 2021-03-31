package com.ikeyit.security.social.weixin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikeyit.security.social.authentication.AppSocialAuthenticationProvider;
import com.ikeyit.security.social.authentication.AppSocialUserInfo;
import com.ikeyit.security.social.authentication.AppSocialUserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

public class MiniProgramAuthenticationProvider extends AppSocialAuthenticationProvider {

    public static final String URL_AUTH = "https://api.weixin.qq.com/sns/jscode2session?appid={appId}&secret={appSecret}&js_code={code}&grant_type=authorization_code";

    private RestTemplate restTemplate;

    private WeixinClientRepository clientRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    public MiniProgramAuthenticationProvider(WeixinClientRepository clientRepository, AppSocialUserService appSocialUserService) {
        super(appSocialUserService);
        this.restTemplate = WeixinRestTemplate.get();
        this.clientRepository = clientRepository;
    }

    @Override
    protected Authentication doAuthenticate(Authentication authentication) throws AuthenticationException {
        MiniProgramAuthenticationToken authenticationToken = (MiniProgramAuthenticationToken) authentication;
        String appId = authenticationToken.getAppId();
        String code = authenticationToken.getCode();
        String encryptedUserInfo = authenticationToken.getUserInfo();
        String iv = authenticationToken.getIv();

        WeixinClient weixinClient = clientRepository.getClient(appId);
        if (weixinClient == null)
            throw new BadCredentialsException("微信客户端未授权，appId：" + appId);

        String appSecret = weixinClient.getAppSecret();
        JsonNode response = null;
        try {
            response = restTemplate.getForObject(URL_AUTH, JsonNode.class, appId, appSecret, code);
            int errcode = response.path("errcode").asInt(0);
            if (errcode != 0)
                throw new BadCredentialsException("微信授权验证失败：" + errcode + ", " + response.path("errmsg").asText());

            String session_key = response.path("session_key").asText();
//            String unionid = response.path("unionid").asText();
//            String openid = response.path("openid").asText();
            //如果已经有unionid，尽快返回？
//            if (StringUtils.hasText(unionid)) {
//                return new MiniProgramAuthenticationToken(new AppSocialUserInfo(unionid, session_key, "weixin", appId, ""),null);
//            }

            if (!StringUtils.hasText(encryptedUserInfo) || !StringUtils.hasText(iv))
                throw new BadCredentialsException("小程序客户端应该提供加密的用户信息");

            //解密userInfo，以便获得unionId
            String data = WxSecurityUtils.decryptUserInfo(session_key, encryptedUserInfo, iv);

            JsonNode userInfoJson = objectMapper.readValue(data, JsonNode.class);
            String unionId = userInfoJson.path("unionId").asText();
            String openId = userInfoJson.path("openId").asText();
            String nickName = userInfoJson.path("nickName").asText();
            String avatarUrl = userInfoJson.path("avatarUrl").asText();

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

            return new MiniProgramAuthenticationToken(appSocialUserInfo,null);
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception ex) {
            throw new BadCredentialsException("微信接口故障", ex);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MiniProgramAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
