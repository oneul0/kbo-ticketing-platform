package com.boeingmerryho.business.membershipservice.presentation.dto.response;

public record MembershipUserDetailAdminResponseDto(
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
