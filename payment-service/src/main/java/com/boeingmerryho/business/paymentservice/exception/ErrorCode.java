package com.boeingmerryho.business.paymentservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {
	PAYMENT_NOT_FOUND("해당 결제를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	PAYMENT_DETAIL_NOT_FOUND("해당 결제 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	PAYMENT_REFUND_REQUEST_FAIL("해당 건에 대한 환불 요청이 불가능합니다.", HttpStatus.BAD_REQUEST),
	;

	private final String message;
	private final HttpStatus status;

	@Override
	public HttpStatus getStatus() {
		return this.status;
	}

	@Override
	public String getErrorCode() {
		return this.name();
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
