package com.boeingmerryho.business.paymentservice.application.dto.request;

import org.springframework.data.domain.Pageable;

public record PaymentDetailSearchRequestServiceDto(
	Pageable customPageable,
	Long id,
	Long paymentId,
	Boolean isDeleted
) {
}
