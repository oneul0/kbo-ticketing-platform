package com.boeingmerryho.business.seatservice.application.dto.response;

public record SeatActiveServiceResponseDto(
	Long id,
	String name,
	Integer seatBlock,
	Integer seatColumn,
	Integer seatRow,
	String seatNo,
	Boolean isActive
) {
}