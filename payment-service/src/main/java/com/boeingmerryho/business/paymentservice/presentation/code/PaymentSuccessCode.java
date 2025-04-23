package com.boeingmerryho.business.paymentservice.presentation.code;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.response.SuccessCode;

public enum PaymentSuccessCode implements SuccessCode {
	FETCHED_PAYMENT_DETAIL("결제 내역을 조회했습니다.", HttpStatus.OK),
	TICKET_REFUND_REQUESTED("티켓 결제 환불 요청이 접수되었습니다.", HttpStatus.ACCEPTED),
	MEMBERSHIP_REFUND_REQUESTED("멤버십 결제 환불 요청이 접수되었습니다.", HttpStatus.ACCEPTED),
	PAYMENT_READY_REQUESTED("결제 준비 요청이 완료되었습니다.", HttpStatus.ACCEPTED),
	PAYMENT_APPROVED("결제가 승인되었습니다.", HttpStatus.OK),
	PAYMENT_CANCELED("해당 결제가 취소되었습니다", HttpStatus.OK),
	MEMBERSHIP_REFUNDED("멤버십 결제 환불이 성공적으로 처리되었습니다.", HttpStatus.OK),
	TICKET_REFUNDED("티켓 결제 환불이 성공적으로 처리되었습니다.", HttpStatus.OK),
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
