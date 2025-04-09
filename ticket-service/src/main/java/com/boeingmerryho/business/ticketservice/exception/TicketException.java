package com.boeingmerryho.business.ticketservice.exception;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.Getter;

@Getter
public class TicketException extends GlobalException {

	private final ErrorCode errorCode;

	public TicketException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}
}
