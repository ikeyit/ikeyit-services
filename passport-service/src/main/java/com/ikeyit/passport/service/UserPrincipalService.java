package com.ikeyit.passport.service;

import com.ikeyit.common.utils.IdUtils;
import com.ikeyit.passport.domain.*;
import com.ikeyit.passport.repository.*;
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
import java.util.stream.Stream;

/**
 * 认证用户服务。
 * 图省事，用户名密码/第三方社交/手机验证码登录合并实现
 *
 */
@Service
public class UserPrincipalService implements UserDetailsService, MobileUserService, AppSocialUserService, RefreshTokenUserService {

    private static Logger log = LoggerFactory.getLogger(UserPrincipalService.class);

    UserRepository userRepository;

    UserConnectionRepository userConnectionRepository;

    WeixinConnectionRepository weixinConnectionRepository;

    PermissionRepository permissionRepository;

    RoleRepository roleRepository;

    @Autowired
    public UserPrincipalService(UserRepository userRepository,
                                UserConnectionRepository userConnectionRepository,
                                WeixinConnectionRepository weixinConnectionRepository,
                                PermissionRepository permissionRepository,
                                RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userConnectionRepository = userConnectionRepository;
        this.weixinConnectionRepository = weixinConnectionRepository;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * 根据登录名查找用户
     * @param loginName
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(loginName))
            throw new UsernameNotFoundException("用户名为空");
        User user = userRepository.getByLoginName(loginName);
        if (user == null)
            throw new UsernameNotFoundException("用户不存在");

        return buildUserPrincipal(user);
    }

    /**
     * 根据手机号查找用户
     * @param mobile
     * @return
     * @throws UsernameNotFoundException
     */
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

    /**
     * 实现RefreshTokenUserService，根据jwt中的subject返回用户，实际为用户ID
     * @param subject
     * @return
     */
    @Override
    public UserDetails loadUserBySubject(String subject) {
        if (StringUtils.isEmpty(subject))
            throw new UsernameNotFoundException("subject为空");
        User user = userRepository.getById(Long.parseLong(subject));
        if (user == null)
            throw new UsernameNotFoundException("用户不存在");
        return buildUserPrincipal(user);
    }


    /**
     * 使用第三方登录时，如果时初次登录，则创建一个关联的默认本地用户
     * @param mobile
     * @param nick
     * @param avatar
     * @return
     */
    private User createDefaultUser(String mobile, String nick, String avatar) {
        User user = new User();
        user.setLoginName(IdUtils.uuid());
        user.setMobile(mobile);
        user.setAvatar(avatar);
        user.setEnabled(Boolean.TRUE);
        user.setNick(nick);
        user.setVerified(Boolean.TRUE);
        userRepository.create(user);
        return user;
    }


    /**
     * 根据第三方登录结果，返回对应的本地用户
     * @param appSocialUserInfo
     * @return
     */
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
        if (appSocialUserInfo.getProvider().startsWith("weixin-")) {
            String unionName = (String) appSocialUserInfo.getExtra("unionName");
            if (unionName != null) {
                String openId = (String)appSocialUserInfo.getExtra("openId");
                String appName = (String)appSocialUserInfo.getExtra("appName");
                String appId = (String)appSocialUserInfo.getExtra("appId");
                WeixinConnection weixinConnection = weixinConnectionRepository.getByOpenId(openId);
                if (weixinConnection == null) {
                    weixinConnection = new WeixinConnection();
                    weixinConnection.setUserId(user.getId());
                    weixinConnection.setAppId(appId);
                    weixinConnection.setUnionId(appSocialUserInfo.getProviderUserId());
                    weixinConnection.setOpenId(openId);
                    weixinConnectionRepository.create(weixinConnection);
                } else {
                    weixinConnectionRepository.update(weixinConnection);
                }
            }
        }
        return buildUserPrincipal(user);
    }

    private UserPrincipal buildUserPrincipal(User user) {
        Set<GrantedAuthority> grantedAuthorities = getAuthorities(user.getId());
        return new UserPrincipal(user.getId(),user.getLoginName(),user.getPassword(),
                user.getNick(), user.getAvatar(),user.isEnabled(),true,true,true,
                grantedAuthorities);
    }

    /**
     * 获取用户的权限。
     * 角色和权限都会包含在返回的集合里。角色以r_开头，权限以p_开头
     * @param userId
     * @return
     */
    private Set<GrantedAuthority> getAuthorities(Long userId) {
        List<Role> roles = roleRepository.getUserRoles(userId);
        Stream<GrantedAuthority> grantedRoles = roles.stream()
                .map(item-> new SimpleGrantedAuthority("r_" + item.getName()));

        List<Permission> permissions = permissionRepository.getUserPermissions(userId);
        Stream<GrantedAuthority> grantedPermissions = permissions.stream()
                .map(item-> new SimpleGrantedAuthority("p_" + item.getName()));

        Set<GrantedAuthority>  grantedAuthorities = Stream.concat(grantedRoles, grantedPermissions).collect(Collectors.toSet());
        return grantedAuthorities;
    }

}
