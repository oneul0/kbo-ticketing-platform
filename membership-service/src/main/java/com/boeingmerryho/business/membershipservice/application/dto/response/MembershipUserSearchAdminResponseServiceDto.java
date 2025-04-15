package com.boeingmerryho.business.membershipservice.application.dto.response;

public record MembershipUserSearchAdminResponseServiceDto(
	Long id,
	Long userId,
	Long membershipId,
	Integer season,
	String name,
	Double discount,
	Boolean isActive,
	Boolean isDeleted
) {
}
