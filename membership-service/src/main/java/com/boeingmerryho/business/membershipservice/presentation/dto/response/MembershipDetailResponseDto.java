package com.boeingmerryho.business.membershipservice.presentation.dto.response;

public record MembershipDetailResponseDto(
	Long id,
	Integer season,
	String name,
	Double discount,
	Integer availableQuantity,
	Integer price
) {
}
