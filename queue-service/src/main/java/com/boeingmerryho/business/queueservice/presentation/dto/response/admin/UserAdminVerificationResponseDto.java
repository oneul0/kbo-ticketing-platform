package com.boeingmerryho.business.queueservice.presentation.dto.response.admin;

public record UserAdminVerificationResponseDto(boolean success, String message) {
	public static UserAdminVerificationResponseDto success(String message) {
		return new UserAdminVerificationResponseDto(true, message);
	}

	public static UserAdminVerificationResponseDto failure(String message) {
		return new UserAdminVerificationResponseDto(false, message);
	}
}
