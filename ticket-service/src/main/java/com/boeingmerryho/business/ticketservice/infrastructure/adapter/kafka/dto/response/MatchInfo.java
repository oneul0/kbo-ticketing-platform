package com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response;

public record MatchInfo(
	String id,
	String homeTeamId,
	String awayTeamId,
	String matchDay,
	String stadiumId
) {
}