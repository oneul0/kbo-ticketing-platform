package com.boeingmerryho.business.paymentservice.presentation.dto.request;

public record PaymentCreationRequestDto(
	Long userId,
	Integer totalPrice,
	String paymentType
) {
}
