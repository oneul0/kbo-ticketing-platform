package com.boeingmerryho.business.membershipservice.application.dto.response;

public record MembershipSearchAdminResponseServiceDto(
	Long id,
	Integer season,
	String name,
	Double discount,
	Integer availableQuantity,
	Integer price,
	Boolean isDeleted
) {
}
