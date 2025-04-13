package com.boeingmerryho.business.paymentservice.infrastructure.exception;

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
	PAYMENT_FAILED("결제에 실패했습니다. 다시 시도해주세요,", HttpStatus.INTERNAL_SERVER_ERROR),
	PAYMENT_ALREADY_REFUNDED("이미 환불된 결제 건입니다.", HttpStatus.BAD_REQUEST),
	PAYMENT_REFUND_UNAVAILABLE("환불 요청이 접수되지 않은 결제 건입니다.", HttpStatus.BAD_REQUEST),
	PAYMENT_TICKET_NOT_FOUND("결제된 티켓 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	PAYMENT_MEMBERSHIP_NOT_FOUND("결제된 멤버십 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	PAYMENT_UNSUPPORTED("지원하지 않는 결제 방식입니다.", HttpStatus.BAD_REQUEST),
	PAYMENT_INFO_NOT_FOUND("결제 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
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
