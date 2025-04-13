package com.boeingmerryho.business.membershipservice.application.dto.response;

public record MembershipUserCreateResponseServiceDto(
	Long membershipId,
	Long userId,
	Long paymentId
) {
}
