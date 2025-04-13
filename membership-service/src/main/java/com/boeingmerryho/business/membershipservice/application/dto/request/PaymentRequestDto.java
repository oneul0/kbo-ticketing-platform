package com.boeingmerryho.business.membershipservice.application.dto.request;

public record PaymentRequestDto(
	Long userId,
	Long membershipId
) {
}