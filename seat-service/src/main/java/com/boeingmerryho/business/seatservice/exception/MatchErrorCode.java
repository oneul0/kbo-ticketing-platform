package com.boeingmerryho.business.seatservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;

public enum MatchErrorCode implements BaseErrorCode {
	NOT_FOUND_MATCH(HttpStatus.NOT_FOUND, "MATCH_001", "존재하지 않는 경기 정보입니다."),
	NOT_MATCH_DATE(HttpStatus.BAD_REQUEST, "MATCH_002", "경기 날짜와 요청 날짜가 일치하지 않습니다.");

	private final HttpStatus status;
	private final String errorCode;
	private final String message;

	MatchErrorCode(HttpStatus status, String errorCode, String message) {
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