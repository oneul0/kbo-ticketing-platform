package com.boeingmerryho.business.paymentservice.domain.context;

import org.springframework.data.domain.Pageable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentDetailSearchContext {
	private final Long id;
	private final Long paymentId;
	private final Boolean isDeleted;
	private final Pageable customPageable;
}
