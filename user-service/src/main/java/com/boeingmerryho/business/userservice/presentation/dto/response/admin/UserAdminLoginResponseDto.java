package com.boeingmerryho.business.userservice.presentation.dto.response.admin;

public record UserAdminLoginResponseDto(String accessToken, String refreshToken) {
	public static UserAdminLoginResponseDto fromTokens(String accessToken, String refreshToken) {
		return new UserAdminLoginResponseDto(accessToken, refreshToken);
	}
}
