package com.boeingmerryho.business.seatservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;

public enum SeatErrorCode implements BaseErrorCode {
	NOT_FOUND_SEAT(HttpStatus.NOT_FOUND, "SEAT_001", "존재하지 않는 좌석 정보입니다.");

	private final HttpStatus status;
	private final String errorCode;
	private final String message;

	SeatErrorCode(HttpStatus status, String errorCode, String message) {
		this.status = status;
		this.errorCode = errorCode;
		this.message = message;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public String getMessage() {
		return message;
	}
}