package com.boeingmerryho.business.paymentservice.presentation.dto.request;

public record PaymentCreationRequestDto(
	Integer totalPrice,
	String paymentType
) {
}
