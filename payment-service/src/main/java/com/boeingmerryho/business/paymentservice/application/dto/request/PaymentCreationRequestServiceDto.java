package com.boeingmerryho.business.paymentservice.application.dto.request;

import java.time.LocalDateTime;

public record PaymentCreationRequestServiceDto(
	Long userId,
	Integer totalPrice,
	String paymentType,
	LocalDateTime expiredTime
) {
}
