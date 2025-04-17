package com.boeingmerryho.business.ticketservice.presentation.admin.dto.response;

public record AdminTicketResponseDto(
	Long id,
	Long matchId,
	Long seatId,
	Long userId,
	String ticketNo,
	Integer price,
	String status,
	Boolean isDeleted
) {
}
