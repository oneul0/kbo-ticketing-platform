package com.boeingmerryho.business.userservice.application;

public interface UserJwtHelper {

	void isValidRefreshToken(String refreshToken);

	Long getUserIdFromToken(String refreshToken);

	String generateAccessToken(Long userId);
}

