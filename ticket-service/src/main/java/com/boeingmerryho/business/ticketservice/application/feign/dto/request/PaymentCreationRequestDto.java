package com.boeingmerryho.business.ticketservice.application.feign.dto.request;

import java.time.LocalDateTime;

public record PaymentCreationRequestDto(
	Long userId,
	Integer price,
	Integer quantity,
	String paymentType,
	LocalDateTime expiredTime
) {
}