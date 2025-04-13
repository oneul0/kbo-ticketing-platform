package com.boeingmerryho.business.ticketservice.presentation.user.dto.response;

public record TicketResponseDto(
	Long id,
	Long matchId,
	Long seatId,
	Long userId,
	String ticketNo,
	String status
) {
}
