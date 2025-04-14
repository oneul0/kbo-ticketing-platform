package com.boeingmerryho.business.paymentservice.presentation.dto.request;

import java.util.List;

public record PaymentApproveRequestDto(
	Long paymentId,
	List<Ticket> tickets,
	Long membershipId
) {
}
