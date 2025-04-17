package com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.request;

import java.util.List;

public record SeatListDto(
	String matchDate,
	List<String> seatIds
) {
}
