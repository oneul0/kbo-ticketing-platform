package com.boeingmerryho.business.seatservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;

public enum SeatErrorCode implements BaseErrorCode {
	NOT_FOUND_SEAT(HttpStatus.NOT_FOUND, "SEAT_001", "존재하지 않는 좌석 정보입니다."),
	NOT_FOUND_BLOCK(HttpStatus.NOT_FOUND, "SEAT_002", "존재하지 않는 블록 정보입니다."),
	NOT_EXCEED_4_SEAT(HttpStatus.BAD_REQUEST, "SEAT_003", "4개 이상의 좌석을 예약할 수 없습니다."),
	FAILED_GET_LOCK(HttpStatus.BAD_REQUEST, "SEAT_004", "락 획득에 실패하였습니다."),
	FAILED_PROCESS_SEAT(HttpStatus.BAD_REQUEST, "SEAT_005", "좌석 선점에 실패하였습니다."),
	INVALID_ACCESS(HttpStatus.BAD_REQUEST, "SEAT_006", "잘못된 접근입니다."),
	START_GAME_SEAT_NOT_PROCESS(HttpStatus.BAD_REQUEST, "SEAT_007", "게임이 시작된 후 선점이 불가능합니다."),
	NOT_ACCESS_BEFORE_TODAY(HttpStatus.BAD_REQUEST, "SEAT_008", "오늘보다 이전 날짜를 선택할 수 없습니다."),
	NOT_OVER_1_WEEK(HttpStatus.BAD_REQUEST, "SEAT_009", "요청 날짜를 1주일 이상 넘길 수 없습니다."),
	NOT_OPEN_RESERVATION(HttpStatus.BAD_REQUEST, "SEAT_010", "아직 오픈되지 않은 예약입니다."),
	ALREADY_PROCESS_SEAT(HttpStatus.BAD_REQUEST, "SEAT_011", "이미 선점된 좌석입니다.");

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