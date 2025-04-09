package com.boeingmerryho.business.ticketservice.presentation.admin.dto.request;

public record AdminTicketSearchRequestDto(
	Long id,
	Long matchId,
	Long seatId,
	Long userId,
	String ticketNo,
	String status,
	Boolean isDeleted
) {
}
