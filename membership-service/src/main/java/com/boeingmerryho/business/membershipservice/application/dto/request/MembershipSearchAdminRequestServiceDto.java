package com.boeingmerryho.business.membershipservice.application.dto.request;

import org.springframework.data.domain.Pageable;

public record MembershipSearchAdminRequestServiceDto(
	Pageable customPageable,
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
