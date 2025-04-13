package com.boeingmerryho.business.ticketservice.presentation.user.dto.request;

public record TicketSearchRequestDto(
	Long matchId,
	Long seatId,
	Long userId,
	String ticketNo,
	String status
) {
}
