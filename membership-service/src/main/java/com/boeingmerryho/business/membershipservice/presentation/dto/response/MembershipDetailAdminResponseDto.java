package com.boeingmerryho.business.membershipservice.presentation.dto.response;

public record MembershipDetailAdminResponseDto(
	Long id,
	Integer season,
	String name,
	Double discount,
	Integer availableQuantity,
	Boolean isDeleted
) {
}
