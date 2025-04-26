package com.boeingmerryho.infrastructure.gatewayservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * JWT 에러 코드
 * 형식 : GW-0**
 * */

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements BaseErrorCode {

	JWT_NOT_FOUND("GW-001", "JWT 토큰이 필요합니다.", HttpStatus.UNAUTHORIZED),
	JWT_BLACKLISTED("GW-002", "JWT가 블랙리스트에 존재합니다.", HttpStatus.UNAUTHORIZED),
	JWT_EXPIRED("GW-003", "JWT가 만료되었습니다.", HttpStatus.UNAUTHORIZED),
	WRONG_JWT("GW-004", "잘못된 JWT입니다.", HttpStatus.UNAUTHORIZED),
	JWT_VERIFIED_FAIL("GW-005", "JWT 검증 실패", HttpStatus.UNAUTHORIZED),

	INVALID_REQUEST_BODY("GW-006", "Login Request body 파싱에 실패했습니다.", HttpStatus.UNAUTHORIZED),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
