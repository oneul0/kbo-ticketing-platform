package com.boeingmerryho.business.paymentservice.application.dto.request;

public record PaymentTicketCancelRequestServiceDto(
	Long userId,
	Long id
) {
}
