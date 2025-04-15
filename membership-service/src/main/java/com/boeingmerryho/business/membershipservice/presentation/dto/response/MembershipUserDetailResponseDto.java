package com.boeingmerryho.business.membershipservice.presentation.dto.response;

public record MembershipUserDetailResponseDto(
	Integer season,
	String name,
	Double discount,
	Boolean isActive
) {
}
