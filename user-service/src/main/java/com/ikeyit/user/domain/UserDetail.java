package com.ikeyit.user.domain;

import java.util.Objects;

public class UserDetail {
    Long id;

    String loginName;

    String mobile;

    String email;

    String nick;

    String avatar;

    Integer sex = 0;

    String location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDetail)) return false;
        UserDetail that = (UserDetail) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(loginName, that.loginName) &&
                Objects.equals(mobile, that.mobile) &&
                Objects.equals(email, that.email) &&
                Objects.equals(nick, that.nick) &&
                Objects.equals(avatar, that.avatar) &&
                Objects.equals(sex, that.sex) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, loginName, mobile, email, nick, avatar, sex, location);
    }

    @Override
    public String toString() {
        return "UserDetail{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", nick='" + nick + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sex=" + sex +
                ", location='" + location + '\'' +
                '}';
    }
}
