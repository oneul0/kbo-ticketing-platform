package com.boeingmerryho.business.membershipservice.presentation.dto.response;

public record MembershipUserCreateResponseDto(
	Long membershipId,
	Long userId,
	Long paymentId
) {
}
