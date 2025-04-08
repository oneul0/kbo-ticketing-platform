package com.boeingmerryho.business.storeservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;

public enum StoreErrorCode implements BaseErrorCode {

	ALREADY_REGISTERED(HttpStatus.CONFLICT, "STORE_001", "이미 등록된 매장입니다."),
	;

	private final HttpStatus status;
	private final String errorCode;
	private final String message;

	StoreErrorCode(HttpStatus status, String errorCode, String message) {
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
