package com.boeingmerryho.business.paymentservice.application.dto.response;

import java.time.LocalDateTime;

public record PaymentReadyResponseServiceDto(
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
