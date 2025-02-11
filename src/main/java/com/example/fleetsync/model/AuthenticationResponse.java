package com.example.fleetsync.model;

public class AuthenticationResponse {
	private String token;
	private String role;
	private String refreshToken;

	public String getToken() {
		return token;
	}

	public AuthenticationResponse(String token,String role,String refreshToken) {
		this.token = token;
		this.role = role;
		this.refreshToken = refreshToken;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
