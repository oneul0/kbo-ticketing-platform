package com.boeingmerryho.infrastructure.gatewayservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;
import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<?> hubExceptionHandle(GlobalException ex) {
		BaseErrorCode errorCode = ex.getErrorCode();
		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode.getErrorCode(), errorCode.getMessage()));
	}

	@Getter
	@AllArgsConstructor
	private static class ErrorResponse {
		private final String errorCode;
		private final String message;

		static ErrorResponse of(String errorCode, String message) {
			return new ErrorResponse(errorCode, message);
		}
	}
}
