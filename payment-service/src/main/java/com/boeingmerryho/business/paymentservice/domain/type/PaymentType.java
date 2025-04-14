package com.boeingmerryho.business.paymentservice.domain.type;

import java.util.Arrays;

import com.boeingmerryho.business.paymentservice.infrastructure.exception.ErrorCode;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.PaymentException;

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

	public static PaymentType from(String name) {
		return Arrays.stream(values())
			.filter(v -> v.name().equalsIgnoreCase(name))
			.findFirst()
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_UNSUPPORTED));
	}
}
