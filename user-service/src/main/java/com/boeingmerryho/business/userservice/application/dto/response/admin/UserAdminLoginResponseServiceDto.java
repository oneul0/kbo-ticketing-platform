package com.boeingmerryho.business.userservice.application.dto.response.admin;

public record UserAdminLoginResponseServiceDto(String accessToken, String refreshToken) {
	public static UserAdminLoginResponseServiceDto fromTokens(String accessToken, String refreshToken) {
		return new UserAdminLoginResponseServiceDto(accessToken, refreshToken);
	}
}
