package com.boeingmerryho.business.paymentservice.presentation.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record PaymentReadyRequestDto(
	@NotBlank Integer price,
	@NotBlank String type,
	@NotBlank String method,
	List<String> tickets,
	@NotBlank String discountType,
	Long membershipId,
	@NotBlank Long paymentId
) {
}
