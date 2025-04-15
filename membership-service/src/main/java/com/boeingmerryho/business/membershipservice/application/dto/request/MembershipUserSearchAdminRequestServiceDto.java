package com.boeingmerryho.business.membershipservice.application.dto.request;

import org.springframework.data.domain.Pageable;

public record MembershipUserSearchAdminRequestServiceDto(
	Pageable customPageable,
	Long userId,
	Long membershipId,
	Integer season,
	String name,
	Double minDiscount,
	Double maxDiscount,
	Boolean isDeleted
) {
}
