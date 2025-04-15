package com.boeingmerryho.business.membershipservice.application.dto.query;

public record MembershipUserSearchCondition(
	Long userId,
	Long membershipId,
	Integer season,
	String name,
	Double minDiscount,
	Double maxDiscount,
	Boolean isDeleted
) {
}
