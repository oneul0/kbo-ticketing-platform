package com.boeingmerryho.business.seatservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;

public enum MembershipErrorCode implements BaseErrorCode {
	INVALID_MEMBERSHIP(HttpStatus.BAD_REQUEST, "MEMBERSHIP_001", "유효하지 않은 멤버십 정보입니다."),
	NOT_FOUND_MEMBERSHIP(HttpStatus.NOT_FOUND, "MEMBERSHIP_002", "존재하지 않는 멤버십 정보입니다.");

	private final HttpStatus status;
	private final String errorCode;
	private final String message;

	MembershipErrorCode(HttpStatus status, String errorCode, String message) {
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