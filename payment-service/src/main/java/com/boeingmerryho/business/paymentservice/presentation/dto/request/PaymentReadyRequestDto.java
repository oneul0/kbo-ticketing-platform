package com.boeingmerryho.business.paymentservice.presentation.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record PaymentReadyRequestDto(
	@NotBlank Integer price,
	@NotBlank String type,
	@NotBlank String method,
	@NotBlank String discountType,
	@NotBlank Long paymentId,
	List<Ticket> tickets,
	Long membershipId
) {
}
