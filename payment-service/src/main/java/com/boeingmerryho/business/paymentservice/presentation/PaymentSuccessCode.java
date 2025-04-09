package com.boeingmerryho.business.paymentservice.presentation;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.response.SuccessCode;

public enum PaymentSuccessCode implements SuccessCode {
	FETCHED_PAYMENT_DETAIL("결제 내역을 조회했습니다.", HttpStatus.OK),
	;

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
