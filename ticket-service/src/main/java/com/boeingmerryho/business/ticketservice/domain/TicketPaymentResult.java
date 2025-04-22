package com.boeingmerryho.business.ticketservice.domain;

import java.util.List;

public record TicketPaymentResult(
	Long userId,
	List<String> seatIds
) {
}
