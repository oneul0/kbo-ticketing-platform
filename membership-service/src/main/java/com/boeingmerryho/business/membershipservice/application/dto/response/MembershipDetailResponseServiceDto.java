package com.boeingmerryho.business.membershipservice.application.dto.response;

public record MembershipDetailResponseServiceDto(
	Long id,
	Integer season,
	String name,
	Double discount
) {
}
