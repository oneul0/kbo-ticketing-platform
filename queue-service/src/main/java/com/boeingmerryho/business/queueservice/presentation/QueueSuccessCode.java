package com.boeingmerryho.business.queueservice.presentation;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.response.SuccessCode;

public enum QueueSuccessCode implements SuccessCode {
	QUEUE_JOIN_SUCCESS("대기열 등록에 성공했습니다.", HttpStatus.OK),
	QUEUE_GET_SEQUENCE_SUCCESS("대기열 순서 조회에 성공했습니다.", HttpStatus.OK),
	QUEUE_CANCEL_SUCCESS("대기열 등록 취소에 성공했습니다.", HttpStatus.OK),

	QUEUE_DELETE_USER_SUCCESS("사용자를 대기열에서 삭제했습니다.", HttpStatus.OK),
	QUEUE_CALL_SUCCESS("다음 사용자 호출에 성공했습니다.", HttpStatus.OK),
	QUEUE_SEARCH_STATUS_SUCCESS("가게 대기열 검색에 성공했습니다.", HttpStatus.OK),
	QUEUE_HISTORY_SEARCH_SUCCESS("가게 대기열 기록 검색에 성공했습니다.", HttpStatus.OK),

	QUEUE_HISTORY_DELETE_SUCCESS("가게 대기열 기록 삭제에 성공했습니다.", HttpStatus.OK),
	QUEUE_HISTORY_UPDATE_SUCCESS("가게 대기열 기록 수정에 성공했습니다.", HttpStatus.OK),
	;

	private final String message;
	private final HttpStatus status;

	QueueSuccessCode(String message, HttpStatus status) {
		this.message = message;
		this.status = status;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
