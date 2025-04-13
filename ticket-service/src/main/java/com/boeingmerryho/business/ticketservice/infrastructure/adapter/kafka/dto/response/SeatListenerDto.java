package com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response;

import java.util.List;

public record SeatListenerDto(
	MatchInfo matchInfo,
	List<SeatInfo> seatsInfo
) {
}