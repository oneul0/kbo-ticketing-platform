package com.boeingmerryho.business.paymentservice.application.dto.request;

public record PaymentMembershipCancelRequestServiceDto(
	Long userId,
	Long id
) {
}
