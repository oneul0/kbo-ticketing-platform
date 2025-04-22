package com.boeingmerryho.business.paymentservice.domain.type;

import java.util.Arrays;

import com.boeingmerryho.business.paymentservice.infrastructure.exception.PaymentException;
import com.boeingmerryho.business.paymentservice.presentation.code.PaymentErrorCode;

import lombok.Getter;

@Getter
public enum DiscountType {
	UNASSIGNED("확인되지 않음"),
	MEMBERSHIP("멤버십"),
	NONE("할인수단 없음"),
	;

	private final String description;

	DiscountType(String description) {
		this.description = description;
	}

	public static DiscountType from(String name) {
		return Arrays.stream(values())
			.filter(v -> v.name().equalsIgnoreCase(name))
			.findFirst()
			.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_UNSUPPORTED));
	}
}
