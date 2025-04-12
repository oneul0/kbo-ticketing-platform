package com.boeingmerryho.business.membershipservice.application.dto.request;

public record MembershipCreateRequestServiceDto(
	Integer season,
	String name,
	Double discount
) {
}
