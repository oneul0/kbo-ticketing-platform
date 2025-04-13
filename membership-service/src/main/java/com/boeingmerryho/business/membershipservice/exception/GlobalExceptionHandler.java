package com.boeingmerryho.business.membershipservice.exception;

import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

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

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleInvalidEnumValue(HttpMessageNotReadableException ex) {
		if (ex.getCause() instanceof InvalidFormatException formatException) {
			Class<?> targetType = formatException.getTargetType();
			if (targetType.isEnum() && targetType.equals(MembershipType.class)) {
				return ResponseEntity
					.status(MembershipErrorCode.INVALID_MEMBERSHIP_TYPE.getStatus())
					.body(ErrorResponse.of(
						MembershipErrorCode.INVALID_MEMBERSHIP_TYPE.getErrorCode(),
						MembershipErrorCode.INVALID_MEMBERSHIP_TYPE.getMessage()
					));
			}
		}

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ErrorResponse.of(MembershipErrorCode.BAD_REQUEST.getErrorCode(),
				MembershipErrorCode.BAD_REQUEST.getMessage()));
	}

}
