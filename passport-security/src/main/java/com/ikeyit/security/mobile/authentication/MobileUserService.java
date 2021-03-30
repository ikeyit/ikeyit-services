package com.ikeyit.security.mobile.authentication;

import org.springframework.security.core.userdetails.UserDetails;

public interface MobileUserService {
    UserDetails loadUserByMobile(String mobile);
}
