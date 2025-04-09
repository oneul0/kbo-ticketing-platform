package com.boeingmerryho.business.ticketservice.application.admin.dto.request;

public record AdminTicketSearchRequestServiceDto(
	Long id,
	Long matchId,
	Long seatId,
	Long userId,
	String ticketNo,
	String status,
	Boolean isDeleted
) {
}
