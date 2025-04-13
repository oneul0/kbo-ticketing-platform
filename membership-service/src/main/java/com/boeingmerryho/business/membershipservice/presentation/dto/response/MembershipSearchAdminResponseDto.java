package com.boeingmerryho.business.membershipservice.presentation.dto.response;

public record MembershipSearchAdminResponseDto(
	Long id,
	Integer season,
	String name,
	Double discount,
	Integer availableQuantity,
	Boolean isDeleted
) {
}
