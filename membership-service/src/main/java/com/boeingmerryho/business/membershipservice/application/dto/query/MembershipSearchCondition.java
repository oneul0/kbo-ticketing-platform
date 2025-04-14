package com.boeingmerryho.business.membershipservice.application.dto.query;

public record MembershipSearchCondition(
	Integer season,
	String name,
	Double minDiscount,
	Double maxDiscount,
	Integer minAvailableQuantity,
	Integer maxAvailableQuantity,
	Integer minPrice,
	Integer maxPrice,
	Boolean isDeleted
) {
}
