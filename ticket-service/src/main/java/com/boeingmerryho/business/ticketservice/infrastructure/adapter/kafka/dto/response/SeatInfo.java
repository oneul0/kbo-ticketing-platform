package com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response;

public record SeatInfo(
	String id,
	String userId,
	String block,
	String column,
	String row,
	String price,
	String createdAt,
	String expiredAt
) {
}