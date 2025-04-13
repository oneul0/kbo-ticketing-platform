package com.boeingmerryho.business.membershipservice.presentation.dto.request;

public record MembershipUpdateRequestDto(
	Integer season,
	Double discount
) {
}
