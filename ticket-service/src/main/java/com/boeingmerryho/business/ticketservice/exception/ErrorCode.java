package com.boeingmerryho.business.ticketservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {

	TICKET_NOT_FOUND(HttpStatus.NOT_FOUND, "T-001", "해당 티켓을 찾을 수 없습니다."),
	INVALID_TICKET_STATUS(HttpStatus.BAD_REQUEST, "T-002", "유효하지 않은 티켓 상태입니다."),
	TICKET_PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "T-003", "결제 정보를 찾을 수 없습니다."),
	TICKET_PAYMENT_INVALID_FIELD(HttpStatus.BAD_REQUEST, "T-004", "결제 정보가 유효하지 않습니다."),
	;

	private final HttpStatus status;
	private final String errorCode;
	private final String message;

	@Override
	public HttpStatus getStatus() {
		return this.status;
	}

	@Override
	public String getErrorCode() {
		return this.errorCode;
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
