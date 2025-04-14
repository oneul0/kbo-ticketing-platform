package com.boeingmerryho.business.membershipservice.application.dto.request;

public record MembershipUpdateRequestServiceDto(
	Integer season,
	Double discount,
	Integer availableQuantity,
	Integer price
) {
}
