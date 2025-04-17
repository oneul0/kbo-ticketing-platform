package com.boeingmerryho.business.ticketservice.presentation;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.response.SuccessCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TicketSuccessCode implements SuccessCode {

	TICKET_FOUND(HttpStatus.OK, "티켓 조회 성공"),
	TICKET_SEARCH(HttpStatus.OK, "티켓 검색 성공"),
	TICKET_DELETE(HttpStatus.OK, "티켓 삭제 성공"),
	TICKET_STATUS_UPDATE(HttpStatus.OK, "티켓 상태 변경 성공"),
	TICKET_PAYMENT_FOUND(HttpStatus.OK, "결제 정보 조회 성공"),
	;

	private final HttpStatus status;
	private final String message;

	@Override
	public HttpStatus getStatus() {
		return this.status;
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
