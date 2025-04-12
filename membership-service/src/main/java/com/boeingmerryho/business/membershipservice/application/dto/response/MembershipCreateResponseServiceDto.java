package com.boeingmerryho.business.membershipservice.application.dto.response;

public record MembershipCreateResponseServiceDto(
	Long id,
	Integer season,
	String name,
	Double discount
) {
}
