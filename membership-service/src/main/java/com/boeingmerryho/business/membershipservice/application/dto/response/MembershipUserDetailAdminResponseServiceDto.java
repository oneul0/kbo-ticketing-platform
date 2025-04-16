package com.boeingmerryho.business.membershipservice.application.dto.response;

public record MembershipUserDetailAdminResponseServiceDto(
	Long id,
	Long membershipId,
	Long userId,
	Boolean isActive,
	Boolean isDeleted,
	Integer season,
	String name,
	Double discount
) {
}
