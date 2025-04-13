package com.boeingmerryho.business.paymentservice.presentation.dto.request;

import java.time.LocalDateTime;

public record PaymentCreationRequestDto(
	Long userId,
	Integer totalPrice,
	String paymentType,
	LocalDateTime expiredTime
) {
}
