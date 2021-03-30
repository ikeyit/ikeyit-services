package com.ikeyit.passport.domain;

public class Authority {

	Long userId;

	String role;

	public Authority() {

	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}



}
