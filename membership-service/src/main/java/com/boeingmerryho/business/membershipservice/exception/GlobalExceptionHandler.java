package com.boeingmerryho.business.membershipservice.exception;

import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;
import io.github.boeingmerryho.commonlibrary.exception.ErrorResponse;
import io.github.boeingmerryho.commonlibrary.exception.GlobalException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<ErrorResponse> handleGlobalException(GlobalException ex) {
		BaseErrorCode errorCode = ex.getErrorCode();

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode.getErrorCode(), errorCode.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
			.map(FieldError::getDefaultMessage)
			.filter(Objects::nonNull)
			.collect(Collectors.collectingAndThen(
				Collectors.toList(),
				list -> list.isEmpty() ? "잘못된 요청입니다." : String.join(", ", list)
			));

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ErrorResponse.of("VALIDATION_ERROR", message));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleEnumConversion(MethodArgumentTypeMismatchException ex) {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ErrorResponse.of("INVALID_ENUM", "잘못된 상태 값입니다."));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
		if (e.getMessage() != null && e.getMessage().contains("No enum constant")) {
			return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ErrorResponse.of("INVALID_STATUS", "지원하지 않는 상태 값입니다."));
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
}
