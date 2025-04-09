package com.boeingmerryho.business.ticketservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.boeingmerryho.commonlibrary.exception.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(TicketException.class)
	public ResponseEntity<?> ticketExceptionHandler(TicketException e) {
		ErrorCode errorCode = e.getErrorCode();
		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode.getErrorCode(), errorCode.getMessage()));
	}
}
