package com.boeingmerryho.business.paymentservice.presentation.dto.request;

import java.time.LocalDateTime;

public record PaymentCreationRequestDto(
	Long userId,
	Integer price,
	Integer quantity,
	String paymentType,
	LocalDateTime expiredTime
) {
}
