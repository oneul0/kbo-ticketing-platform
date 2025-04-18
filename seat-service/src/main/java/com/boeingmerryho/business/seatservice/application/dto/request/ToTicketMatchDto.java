package com.boeingmerryho.business.seatservice.application.dto.request;

public record ToTicketMatchDto(
	String id,
	String homeTeamId,
	String awayTeamId,
	String matchDay,
	String matchTime,
	String stadiumId
) {
}