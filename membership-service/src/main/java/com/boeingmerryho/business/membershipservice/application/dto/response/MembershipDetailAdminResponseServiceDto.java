package com.boeingmerryho.business.membershipservice.application.dto.response;

public record MembershipDetailAdminResponseServiceDto(
	Long id,
	Integer season,
	String name,
	Double discount,
	Boolean isDeleted
) {
}
