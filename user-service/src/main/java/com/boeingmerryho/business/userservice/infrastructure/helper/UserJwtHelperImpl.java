package com.boeingmerryho.business.userservice.infrastructure.helper;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.userservice.application.UserJwtHelper;
import com.boeingmerryho.business.userservice.application.utils.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserJwtHelperImpl implements UserJwtHelper {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void isValidRefreshToken(String refreshToken) {
		jwtTokenProvider.validateRefreshToken(refreshToken);
	}

	@Override
	public Long getUserIdFromToken(String refreshToken) {
		return Long.valueOf(jwtTokenProvider.getUserIdFromToken(refreshToken));
	}

	@Override
	public String generateAccessToken(Long userId) {
		return jwtTokenProvider.generateAccessToken(userId);
	}

}