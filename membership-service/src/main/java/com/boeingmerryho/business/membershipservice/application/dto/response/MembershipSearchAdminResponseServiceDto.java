package com.boeingmerryho.business.membershipservice.application.dto.response;

public record MembershipSearchAdminResponseServiceDto(
	Long id,
	Integer season,
	String name,
	Double discount,
	Boolean isDeleted
) {
}
