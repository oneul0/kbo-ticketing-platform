package com.boeingmerryho.business.seatservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;

public enum SeatErrorCode implements BaseErrorCode {
	NOT_FOUND_SEAT(HttpStatus.NOT_FOUND, "SEAT_001", "존재하지 않는 좌석 정보입니다."),
	NOT_FOUND_BLOCK(HttpStatus.NOT_FOUND, "SEAT_002", "존재하지 않는 블록 정보입니다."),
	NOT_EXCEED_4_SEAT(HttpStatus.BAD_REQUEST, "SEAT_003", "4개 이상의 좌석을 예약할 수 없습니다."),
	FAILED_GET_LOCK(HttpStatus.BAD_REQUEST, "SEAT_004", "락 획득에 실패하였습니다."),
	FAILED_PROCESS_SEAT(HttpStatus.BAD_REQUEST, "SEAT_005", "좌석 선점에 실패하였습니다.");

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