package com.boeingmerryho.business.membershipservice.application.dto.response;

public record MembershipUserDetailResponseServiceDto(
	Integer season,
	String name,
	Double discount,
	Boolean isActive
) {
}
