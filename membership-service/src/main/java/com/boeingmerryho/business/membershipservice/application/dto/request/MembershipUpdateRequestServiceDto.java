package com.boeingmerryho.business.membershipservice.application.dto.request;

import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;

public record MembershipUpdateRequestServiceDto(
	Integer season,
	MembershipType name,
	Double discount
) {
}
