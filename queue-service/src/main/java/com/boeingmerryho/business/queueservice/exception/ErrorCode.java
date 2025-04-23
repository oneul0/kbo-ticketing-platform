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
	LOCK_ACQUISITION_FAIL("Q-005", "잠금 획득에 실패했습니다.", HttpStatus.CONFLICT),

	WAITLIST_NOT_EXIST("Q-006", "대기열에 존재하지 않는 사용자입니다.", HttpStatus.BAD_REQUEST),
	CAN_NOT_REMOVE_QUEUE("Q-007", "대기열에서 삭제하지 못했습니다.", HttpStatus.BAD_REQUEST),
	WAITLIST_EMPTY("Q-008", "대기열이 없습니다.", HttpStatus.BAD_REQUEST),

	QUEUE_HISTORY_NOT_FOUND("Q-009", "대기열 기록이 없습니다.", HttpStatus.BAD_REQUEST),
	TICKET_NOT_FOUND("Q-010", "요청한 티켓을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),

	USER_ALREADY_IN_QUEUE("Q-011", "대기열에 이미 사용자가 존재합니다.", HttpStatus.BAD_REQUEST),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
