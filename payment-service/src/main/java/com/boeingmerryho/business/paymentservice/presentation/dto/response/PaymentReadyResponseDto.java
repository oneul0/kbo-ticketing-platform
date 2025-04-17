package com.boeingmerryho.business.paymentservice.presentation.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentReadyResponseDto(
	Long paymentId,
	Integer price,
	String accountNumber,
	String accountBank,
	LocalDateTime dueDate,
	String accountHolder,
	String nextRedirectPcUrl,
	LocalDateTime createdAt
) {
}
