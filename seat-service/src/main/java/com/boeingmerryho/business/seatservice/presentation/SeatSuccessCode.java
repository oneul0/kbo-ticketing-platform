package com.boeingmerryho.business.seatservice.presentation;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.response.SuccessCode;

public enum SeatSuccessCode implements SuccessCode {
	OK_SEAT("좌석 조회에 성공했습니다.", HttpStatus.OK),
	CREATED_SEAT("좌석 생성이 완료되었습니다.", HttpStatus.CREATED),
	UPDATED_SEAT("좌석 수정이 완료되었습니다.", HttpStatus.OK),
	DELETED_SEAT("좌석 삭제에 성공하였습니다.", HttpStatus.OK);

	private final String message;
	private final HttpStatus status;

	SeatSuccessCode(String message, HttpStatus status) {
		this.message = message;
		this.status = status;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}
}