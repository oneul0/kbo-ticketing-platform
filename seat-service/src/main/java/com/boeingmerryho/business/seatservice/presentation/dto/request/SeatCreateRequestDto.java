package com.boeingmerryho.business.seatservice.presentation.dto.request;

public record SeatCreateRequestDto(
	String name,
	Integer seatBlock,
	Integer seatColumn,
	Integer seatRow,
	Integer price,
	Boolean isActive
) {
}