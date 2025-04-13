package com.boeingmerryho.business.paymentservice.application.dto.request;

import java.util.List;

import com.boeingmerryho.business.paymentservice.presentation.dto.request.Ticket;

public record PaymentApproveAdminRequestServiceDto(
	Long userId,
	Long paymentId,
	List<Ticket> tickets,
	Long membershipId
) {
}
