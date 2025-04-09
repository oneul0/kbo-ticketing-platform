package com.boeingmerryho.business.paymentservice.presentation;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.response.SuccessCode;

public enum PaymentSuccessCode implements SuccessCode {
	FETCHED_PAYMENT_DETAIL("결제 내역을 조회했습니다.", HttpStatus.OK),
	REQUESTED_REFUND_TICKET("티켓 결제 환불 요청이 접수되었습니다.", HttpStatus.ACCEPTED),
	REQUESTED_REFUND_MEMBERSHIP("멤버십 결제 환불 요청이 접수되었습니다.", HttpStatus.ACCEPTED);

	private final String message;
	private final HttpStatus status;

	PaymentSuccessCode(String message, HttpStatus status) {
		this.message = message;
		this.status = status;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
