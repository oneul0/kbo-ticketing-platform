package com.boeingmerryho.business.seatservice.application.dto.request;

public record ToTicketSeatDto(
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