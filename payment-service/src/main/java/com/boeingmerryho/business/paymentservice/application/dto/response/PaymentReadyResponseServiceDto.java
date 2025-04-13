package com.boeingmerryho.business.paymentservice.application.dto.response;

import java.time.LocalDateTime;

public record PaymentReadyResponseServiceDto(
	Long paymentId,
	String accountNumber,
	String accountBank,
	LocalDateTime dueDate,
	String accountHolder
) {
}
