package com.boeingmerryho.business.membershipservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;

public enum MembershipErrorCode implements BaseErrorCode {

	ALREADY_REGISTERED(HttpStatus.CONFLICT, "MEMBERSHIP_001", "이미 등록된 멤버십입니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBERSHIP_002", "존재하지 않는 멤버십입니다."),
	NO_UPDATE_FIELDS_PROVIDED(HttpStatus.BAD_REQUEST, "MEMBERSHIP_003", "필드 값이 유효하지 않습니다."),
	;

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
