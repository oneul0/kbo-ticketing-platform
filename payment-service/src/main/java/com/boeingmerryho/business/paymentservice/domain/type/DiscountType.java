package com.boeingmerryho.business.paymentservice.domain.type;

import lombok.Getter;

@Getter
public enum DiscountType {
	MEMBERSHIP("멤버십"),
	NONE("할인수단 없음"),
	;

	private final String description;

	DiscountType(String description) {
		this.description = description;
	}
}
