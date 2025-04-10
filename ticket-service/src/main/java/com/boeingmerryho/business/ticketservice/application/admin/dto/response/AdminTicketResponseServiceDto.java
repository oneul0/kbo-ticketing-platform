package com.boeingmerryho.business.ticketservice.application.admin.dto.response;

public record AdminTicketResponseServiceDto(
	Long id,
	Long matchId,
	Long seatId,
	Long userId,
	String ticketNo,
	String status,
	Boolean isDeleted
) {
}
