package com.boeingmerryho.business.membershipservice.presentation.dto.response;

public record MembershipUserCreateResponseDto(
	Long id,
	Long membershipId,
	Long userId,
	Boolean isActive,
	Long paymentId
) {
}
