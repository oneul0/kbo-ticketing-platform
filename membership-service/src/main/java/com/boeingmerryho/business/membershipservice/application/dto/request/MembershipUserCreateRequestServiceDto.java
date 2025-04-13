package com.boeingmerryho.business.membershipservice.application.dto.request;

public record MembershipUserCreateRequestServiceDto(
	Long membershipId,
	Long userId
) {
}
