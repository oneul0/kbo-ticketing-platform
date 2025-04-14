package com.boeingmerryho.business.queueservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LockErrorCode implements BaseErrorCode {
	ALREADY_PROCEED("QL-001", "다른 요청을 처리 중 입니다. 잠시 후 다시 시도해주세요.", HttpStatus.BAD_REQUEST),
	INTERNAL_SERVER_ERROR("QL-002", "서버에 장애가 발생했습니다.", HttpStatus.BAD_REQUEST),

	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
