package com.boeingmerryho.business.seatservice.presentation.dto.response;

public record SeatResponseDto(
	Long id,
	String name,
	Integer seatBlock,
	Integer seatColumn,
	Integer seatRow,
	String seatNo,
	Boolean isActive
) {
}