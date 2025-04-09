package com.boeingmerryho.business.seatservice.application.dto.response;

public record SeatCreateServiceResponseDto(
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