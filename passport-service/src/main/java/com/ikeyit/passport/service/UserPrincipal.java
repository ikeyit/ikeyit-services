package com.ikeyit.passport.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 被认证的用户对象！
 * 提供更多的属性支持，比如用户ID/头像等
 *
 * */
public class UserPrincipal implements UserDetails {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private Long userId;

    private String password;

    private String loginName;

    private String avatar;

    private String nick;

    private Collection<GrantedAuthority> authorities;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private boolean credentialsNonExpired = true;

    private boolean enabled = true;

    public UserPrincipal(Long userId, String loginName, String nick, String avatar, Collection<GrantedAuthority> authorities) {
        this.userId = userId;
        this.loginName = loginName;
        this.nick = nick;
        this.avatar = avatar;
        this.authorities = authorities;
    }

    public UserPrincipal(Long userId, String loginName, String password, String nick, String avatar, boolean enabled,
                         boolean accountNonExpired, boolean credentialsNonExpired,
                         boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "Cannot pass null or empty values to constructor");
        }
        this.userId = userId;
        this.loginName = loginName;
        this.password = password;
        this.nick = nick;
        this.avatar = avatar;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = Collections.unmodifiableCollection(authorities);
    }


    public Long getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId.toString();
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNick() {
        return nick;
    }

    public String getLoginName() {
        return loginName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }



}
