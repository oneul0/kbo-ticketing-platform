package com.boeingmerryho.business.paymentservice.domain.type;

import lombok.Getter;

@Getter
public enum PaymentType {
	TICKET("티켓"),
	MEMBERSHIP("멤버십"),
	;

	private final String description;

	PaymentType(String description) {
		this.description = description;
	}
}
