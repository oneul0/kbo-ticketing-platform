package com.boeingmerryho.business.membershipservice.presentation.dto.request;

import org.springframework.data.domain.Pageable;

public record MembershipSearchAdminRequestServiceDto(
	Pageable customPageable,
	Integer season,
	String name,
	Double minDiscount,
	Double maxDiscount,
	Boolean isDeleted
) {
}
