package com.boeingmerryho.business.paymentservice.application.dto.kafka;

import java.util.List;

public record TicketPaymentEvent(
	String event,
	List<String> tickets
) {
}
