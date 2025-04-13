package com.boeingmerryho.business.paymentservice.application.dto.request;

import java.util.List;

public record PaymentReadyRequestServiceDto(
	Long userId,
	Integer price,
	String type,
	String method,
	List<String> tickets,    // ticketNo list
	String discountType,
	Long membershipId,
	Long paymentId
) {
}
