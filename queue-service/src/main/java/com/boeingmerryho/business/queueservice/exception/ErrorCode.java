package com.boeingmerryho.business.queueservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {
	STORE_IS_NOT_ACTIVATED("Q-001", "등록하려는 가게가 영업 중이 아닙니다.", HttpStatus.BAD_REQUEST),
	TICKET_IS_NOT_ACTIVATED("Q-002", "티켓이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
	USER_IS_NOT_MATCHED("Q-003", "사용자 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

	QUEUE_JOIN_FAIL("Q-004", "알 수 없는 오류로 대기열 등록에 실패했습니다.", HttpStatus.BAD_REQUEST),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
