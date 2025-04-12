package com.boeingmerryho.business.membershipservice.presentation.dto.response;

public record MembershipCreateResponseDto(
	Long id,
	Integer season,
	String name,
	Double discount
) {
}
