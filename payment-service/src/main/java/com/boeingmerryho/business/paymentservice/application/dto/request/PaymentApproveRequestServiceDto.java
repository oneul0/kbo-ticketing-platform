package com.boeingmerryho.business.paymentservice.application.dto.request;

public record PaymentApproveRequestServiceDto(
	Long userId,
	String pgToken,
	Long paymentId
) {
}
