package com.ikeyit.passport.service;

import com.ikeyit.common.utils.IdUtils;
import com.ikeyit.passport.domain.Authority;
import com.ikeyit.passport.domain.User;
import com.ikeyit.passport.domain.UserConnection;
import com.ikeyit.passport.domain.WeixinConnection;
import com.ikeyit.passport.repository.AuthorityRepository;
import com.ikeyit.passport.repository.UserConnectionRepository;
import com.ikeyit.passport.repository.UserRepository;
import com.ikeyit.passport.repository.WeixinConnectionRepository;
import com.ikeyit.security.jwt.RefreshTokenUserService;
import com.ikeyit.security.mobile.authentication.MobileUserService;
import com.ikeyit.security.social.authentication.AppSocialUserInfo;
import com.ikeyit.security.social.authentication.AppSocialUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 认证用户服务。
 * 图省事，用户名密码/第三方社交/手机验证码登录合并实现
 *
 */
@Service
public class UserPrincipalService implements UserDetailsService, MobileUserService, AppSocialUserService, RefreshTokenUserService {

    private static Logger log = LoggerFactory.getLogger(UserPrincipalService.class);

    UserRepository userRepository;

    AuthorityRepository authorityRepository;

    UserConnectionRepository userConnectionRepository;

    WeixinConnectionRepository weixinConnectionRepository;

    @Autowired
    public UserPrincipalService(UserRepository userRepository,
                                AuthorityRepository authorityRepository,
                                UserConnectionRepository userConnectionRepository,
                                WeixinConnectionRepository weixinConnectionRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.userConnectionRepository = userConnectionRepository;
        this.weixinConnectionRepository = weixinConnectionRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(loginName))
            throw new UsernameNotFoundException("用户名为空");
        User user = userRepository.getByLoginName(loginName);
        if (user == null)
            throw new UsernameNotFoundException("用户不存在");

        return buildUserPrincipal(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByMobile(String mobile) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(mobile))
            throw new UsernameNotFoundException("手机号为空");
        User user = userRepository.getByMobile(mobile);
        if (user == null)
            user = createDefaultUser(mobile, null, null);

        return buildUserPrincipal(user);
    }

    @Override
    public UserDetails loadUserBySubject(String subject) {
        if (StringUtils.isEmpty(subject))
            throw new UsernameNotFoundException("subject为空");
        User user = userRepository.getById(Long.parseLong(subject));
        if (user == null)
            throw new UsernameNotFoundException("用户不存在");
        return buildUserPrincipal(user);
    }


    private User createDefaultUser(String mobile, String nick, String avatar) {
        User user = new User();
        user.setLoginName(IdUtils.uuid());
        user.setMobile(mobile);
        user.setAvatar(avatar);
        user.setNick(nick);
        userRepository.create(user);
        Authority authority = new Authority();
        authority.setUserId(user.getId());
        authority.setRole("NEW_USER");
        authorityRepository.create(authority);
        return user;
    }


    @Override
    @Transactional
    public UserPrincipal loadUserBySocialUser(AppSocialUserInfo appSocialUserInfo) {
        UserConnection userConnection = userConnectionRepository.getByProviderUserId(appSocialUserInfo.getProvider(), appSocialUserInfo.getProviderUserId());
        User user = null;

        if (userConnection == null) {
            //新建User和Connection
            userConnection = new UserConnection();
            userConnection.setProvider(appSocialUserInfo.getProvider());
            userConnection.setProviderUserId(appSocialUserInfo.getProviderUserId());
            userConnection.setProviderUserNick(appSocialUserInfo.getNick());
            userConnection.setProviderUserAvatar(appSocialUserInfo.getAvatar());
            user = createDefaultUser(null, userConnection.getProviderUserNick(), userConnection.getProviderUserAvatar());
            userConnection.setUserId(user.getId());
            userConnectionRepository.create(userConnection);
        } else {

            user = userRepository.getById(userConnection.getUserId());
            if (user == null)
                user = createDefaultUser(null, userConnection.getProviderUserNick(), userConnection.getProviderUserAvatar());

            //更新connection记录
            userConnection.setUserId(user.getId());
            userConnection.setProviderUserNick(appSocialUserInfo.getNick());
            userConnection.setProviderUserAvatar(appSocialUserInfo.getAvatar());
            userConnectionRepository.update(userConnection);
        }

        //微信Union方式，记录一下openId unionId
        if ("weixin".equals(appSocialUserInfo.getProvider())) {
            String openId = (String)appSocialUserInfo.getExtra("openId");
            String appName = (String)appSocialUserInfo.getExtra("appName");
            String appId = (String)appSocialUserInfo.getExtra("appId");
            WeixinConnection weixinConnection = weixinConnectionRepository.getByOpenId(openId);
            if (weixinConnection == null) {
                weixinConnection = new WeixinConnection();
                weixinConnection.setUserId(user.getId());
                weixinConnection.setAppId(appId);
                weixinConnection.setAppName(appName);
                weixinConnection.setUnionId(appSocialUserInfo.getProviderUserId());
                weixinConnection.setOpenId(openId);
                weixinConnectionRepository.create(weixinConnection);
            } else {
                weixinConnectionRepository.update(weixinConnection);
            }
        }
        return buildUserPrincipal(user);
    }

    private UserPrincipal buildUserPrincipal(User user) {
        List<Authority> authorities = authorityRepository.getByUserId(user.getId());
        Set<GrantedAuthority> grantedAuthorities = authorities.stream()
                .map(item-> new SimpleGrantedAuthority(item.getRole()))
                .collect(Collectors.toSet());

        return new UserPrincipal(user.getId(),user.getLoginName(),user.getPassword(), user.getNick(), user.getAvatar(),true,true,true,true, grantedAuthorities);
    }


}
