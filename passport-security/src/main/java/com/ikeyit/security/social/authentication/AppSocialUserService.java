package com.ikeyit.security.social.authentication;

import org.springframework.security.core.userdetails.UserDetails;

public interface AppSocialUserService {
    UserDetails loadUserBySocialUser(AppSocialUserInfo appSocialUserInfo);
}
