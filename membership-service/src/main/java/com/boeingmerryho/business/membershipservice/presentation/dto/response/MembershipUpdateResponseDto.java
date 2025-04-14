package com.boeingmerryho.business.membershipservice.presentation.dto.response;

import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;

public record MembershipUpdateResponseDto(
	Long id,
	Integer season,
	MembershipType name,
	Double discount,
	Integer availableQuantity,
	Integer price
) {
}
