package com.boeingmerryho.business.paymentservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.boeingmerryho.commonlibrary.exception.ErrorResponse;

@RestControllerAdvice
public class PaymentExceptionHandler {

	@ExceptionHandler(PaymentException.class)
	public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException e) {
		return ResponseEntity
			.status(e.getErrorCode().getStatus())
			.body(ErrorResponse.of(e.getErrorCode().toString(), e.getMessage()));
	}
}
