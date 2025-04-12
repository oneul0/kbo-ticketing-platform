package com.boeingmerryho.business.membershipservice.presentation.dto.request;

import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;

public record MembershipUpdateRequestDto(
	Integer season,
	MembershipType name,
	Double discount
) {
}
