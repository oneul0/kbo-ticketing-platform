package com.boeingmerryho.business.paymentservice.domain.type;

import java.util.Arrays;

import com.boeingmerryho.business.paymentservice.infrastructure.exception.ErrorCode;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.PaymentException;

import lombok.Getter;

@Getter
public enum PaymentMethod {
	BANK_TRANSFER("무통장입금"),
	CARD("카드"),
	KAKAOPAY("카카오페이"),
	;

	private final String description;

	PaymentMethod(String description) {
		this.description = description;
	}

	public static PaymentMethod from(String name) {
		return Arrays.stream(values())
			.filter(v -> v.name().equalsIgnoreCase(name))
			.findFirst()
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_UNSUPPORTED));
	}
}
