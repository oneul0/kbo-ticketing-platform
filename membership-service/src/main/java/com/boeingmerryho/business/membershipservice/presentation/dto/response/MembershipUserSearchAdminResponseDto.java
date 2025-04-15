package com.boeingmerryho.business.membershipservice.presentation.dto.response;

public record MembershipUserSearchAdminResponseDto(
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
