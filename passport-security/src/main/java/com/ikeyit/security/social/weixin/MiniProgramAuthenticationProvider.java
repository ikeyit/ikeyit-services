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

        WeixinClient miniProgramClient = clientRepository.getClient(appId);
        if (miniProgramClient == null)
            throw new BadCredentialsException("未找到小程序客户端");

        String appSecret = miniProgramClient.getAppSecret();
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

            if(miniProgramClient.getUnionName() != null) {
                if (!StringUtils.hasText(unionId))
                    throw new BadCredentialsException("找不到unionid");
                AppSocialUserInfo appSocialUserInfo = new AppSocialUserInfo(unionId, miniProgramClient.getUnionName(), avatarUrl, nickName);
                appSocialUserInfo.addExtra("openId", openId);
                appSocialUserInfo.addExtra("appId", miniProgramClient.getAppId());
                appSocialUserInfo.addExtra("appName", miniProgramClient.getAppName());
                return new MiniProgramAuthenticationToken(appSocialUserInfo,null);
            } else {
                return new MiniProgramAuthenticationToken(new AppSocialUserInfo(openId, miniProgramClient.getAppName(), avatarUrl, nickName),null);
            }

        } catch (Exception ex) {
            throw new BadCredentialsException("微信接口故障", ex);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MiniProgramAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
