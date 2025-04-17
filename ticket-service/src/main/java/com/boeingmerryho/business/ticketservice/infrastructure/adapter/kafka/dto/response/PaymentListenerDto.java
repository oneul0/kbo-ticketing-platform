package com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response;

import java.util.List;

public record PaymentListenerDto(
	String event,
	List<String> tickets
) {
}
