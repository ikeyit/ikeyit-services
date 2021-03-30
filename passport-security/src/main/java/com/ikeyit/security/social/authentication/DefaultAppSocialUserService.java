package com.ikeyit.security.social.authentication;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class DefaultAppSocialUserService implements AppSocialUserService{

    String testUserName = "user_connected_by_";

    String testRole = "user";

    @Override
    public UserDetails loadUserBySocialUser(AppSocialUserInfo appSocialUserInfo) {
        return User.withUsername(testUserName + appSocialUserInfo.getProvider() + "_"
                    + appSocialUserInfo.getProviderUserId()).password("").authorities(testRole).build();
    }
}
