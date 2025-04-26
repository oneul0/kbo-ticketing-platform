package com.boeingmerryho.infrastructure.gatewayservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 요청 제한 관련 에러 코드
 * 형식 : GW-1**
 * */

@Getter
@RequiredArgsConstructor
public enum RateLimitErrorCode implements BaseErrorCode {

	RATE_LIMIT_ERROR_CODE("GW-100", "요청 제한 횟수에 도달했습니다.", HttpStatus.TOO_MANY_REQUESTS),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
