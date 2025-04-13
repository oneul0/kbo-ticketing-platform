package com.boeingmerryho.business.membershipservice.application.dto.request;

import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;

public record MembershipUserCreateRequestServiceDto(
	MembershipType membershipType,
	Long userId
) {
}
