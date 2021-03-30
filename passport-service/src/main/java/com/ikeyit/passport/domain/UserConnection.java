package com.ikeyit.passport.domain;

import java.time.LocalDateTime;

public class UserConnection {
	private Long userId;
	private String provider;
	private String providerUserId;
	private String providerUserName;
	private String providerUserNick;
	private String providerUserAvatar;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProviderUserId() {
		return providerUserId;
	}

	public void setProviderUserId(String providerUserId) {
		this.providerUserId = providerUserId;
	}

	public String getProviderUserName() {
		return providerUserName;
	}

	public void setProviderUserName(String providerUserName) {
		this.providerUserName = providerUserName;
	}

	public String getProviderUserNick() {
		return providerUserNick;
	}

	public void setProviderUserNick(String providerUserNick) {
		this.providerUserNick = providerUserNick;
	}

	public String getProviderUserAvatar() {
		return providerUserAvatar;
	}

	public void setProviderUserAvatar(String providerUserAvatar) {
		this.providerUserAvatar = providerUserAvatar;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}
}
