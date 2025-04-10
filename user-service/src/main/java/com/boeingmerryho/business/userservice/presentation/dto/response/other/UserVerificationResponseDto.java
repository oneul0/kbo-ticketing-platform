package com.boeingmerryho.business.userservice.presentation.dto.response.other;

public record UserVerificationResponseDto(boolean success, String message) {
	public static UserVerificationResponseDto success(String message) {
		return new UserVerificationResponseDto(true, message);
	}

	public static UserVerificationResponseDto failure(String message) {
		return new UserVerificationResponseDto(false, message);
	}
}
