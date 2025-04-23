package com.boeingmerryho.business.storeservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;

public enum StoreErrorCode implements BaseErrorCode {

	ALREADY_REGISTERED(HttpStatus.CONFLICT, "STORE_001", "이미 등록된 매장입니다."),
	INVALID_STADIUM(HttpStatus.BAD_REQUEST, "STORE_002", "존재하지 않는 구장입니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_003", "존재하지 않는 매장입니다."),
	NO_UPDATE_FIELDS_PROVIDED(HttpStatus.BAD_REQUEST, "STORE_004", "필드 값이 유효하지 않습니다."),
	ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "STORE_005", "이미 마감되었습니다."),
	ALREADY_OPENED(HttpStatus.BAD_REQUEST, "STORE_006", "이미 오픈되었습니다."),
	INVALID_STATUS(HttpStatus.BAD_REQUEST, "STORE_007", "잘못된 상태값입니다."),
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
