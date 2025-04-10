package com.boeingmerryho.business.userservice.application.dto.response;

public record UserLoginResponseServiceDto(String accessToken, String refreshToken) {
	public static UserLoginResponseServiceDto fromTokens(String accessToken, String refreshToken) {
		return new UserLoginResponseServiceDto(accessToken, refreshToken);
	}
}
