package com.boeingmerryho.business.membershipservice.application.dto.response;

import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;

public record MembershipUpdateResponseServiceDto(
	Long id,
	Integer season,
	MembershipType name,
	Double discount
) {
}
