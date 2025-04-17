package com.boeingmerryho.business.ticketservice.application.user.dto.response;

public record TicketResponseServiceDto(
	Long id,
	Long matchId,
	Long seatId,
	Long userId,
	String ticketNo,
	Integer price,
	String status
) {
}
