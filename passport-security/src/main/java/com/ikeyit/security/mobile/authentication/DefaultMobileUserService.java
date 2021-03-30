package com.ikeyit.security.mobile.authentication;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class DefaultMobileUserService implements MobileUserService{

    String testMobile = "11122223333";

    String testRole = "user";

    public DefaultMobileUserService() {
        System.out.println("测试手机号：" + this.testMobile + ", 角色：" + testRole);
    }

    @Override
    public UserDetails loadUserByMobile(String mobile) {
        if (testMobile.equals(mobile))
            return User.withUsername(testMobile).password("").authorities(testRole).build();
        return null;
    }
}
