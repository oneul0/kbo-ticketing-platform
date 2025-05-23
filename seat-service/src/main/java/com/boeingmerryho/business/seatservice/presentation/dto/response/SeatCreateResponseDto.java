package com.boeingmerryho.business.seatservice.presentation.dto.response;

public record SeatCreateResponseDto(
	Long id,
	String name,
	Integer seatBlock,
	Integer seatColumn,
	Integer seatRow,
	String seatNo,
	Integer price,
	Boolean isActive
) {
}