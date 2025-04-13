package com.boeingmerryho.business.ticketservice.application.user.dto.request;

public record TicketSearchRequestServiceDto(
	Long matchId,
	Long seatId,
	Long userId,
	String ticketNo,
	String status
) {
}
