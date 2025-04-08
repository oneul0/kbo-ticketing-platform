package com.boeingmerryho.business.paymentservice.presentation.temp;

import org.springframework.http.ResponseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class SuccessResponse<T> {
	private final String message;
	private final T data;

	public static <T> ResponseEntity<SuccessResponse<T>> of(SuccessCode successCode, T data) {
		return ResponseEntity
			.status(successCode.getStatus())
			.body(SuccessResponse.<T>builder()
				.message(successCode.getMessage())
				.data(data)
				.build());
	}

	public static <T> ResponseEntity<SuccessResponse<T>> of(SuccessCode successCode) {
		return ResponseEntity
			.status(successCode.getStatus())
			.body(SuccessResponse.<T>builder()
				.message(successCode.getMessage())
				.build());
	}
}
