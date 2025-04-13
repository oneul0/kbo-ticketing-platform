package com.boeingmerryho.business.paymentservice.application.dto.request;

public record PaymentCreationRequestServiceDto(
	Long userId,
	Integer totalPrice,
	String paymentType
) {
}
