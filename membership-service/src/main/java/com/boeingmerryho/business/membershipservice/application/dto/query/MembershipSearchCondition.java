package com.boeingmerryho.business.membershipservice.application.dto.query;

public record MembershipSearchCondition(
	Integer season,
	String name,
	Double minDiscount,
	Double maxDiscount,
	Boolean isDeleted
) {

}
