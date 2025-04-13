package com.boeingmerryho.business.membershipservice.application.dto.response;

public record MembershipUserCreateResponseServiceDto(
	Long id,
	Long membershipId,
	Long userId,
	Boolean isActive
) {
}
