package com.boeingmerryho.business.membershipservice.application.dto.request;

import java.time.LocalDateTime;

public record PaymentRequestDto(
	Long userId,
	Integer price,
	Integer quantity,
	String paymentType,
	LocalDateTime expiredTime
) {
}