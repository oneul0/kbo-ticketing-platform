package com.boeingmerryho.business.paymentservice.presentation.dto.response;

import java.time.LocalDateTime;

public record PaymentReadyResponseDto(
	Long paymentId,
	String accountNumber,
	String accountBank,
	LocalDateTime dueDate,
	String accountHolder
) {
}
