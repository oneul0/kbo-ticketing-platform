package com.boeingmerryho.business.paymentservice.application.dto.request;

import java.util.List;

import com.boeingmerryho.business.paymentservice.presentation.dto.request.Ticket;

public record PaymentReadyRequestServiceDto(
	Long userId,
	Integer price,
	String type,
	String method,
	String discountType,
	Long paymentId,
	List<Ticket> tickets,
	Long membershipId
) {
}
