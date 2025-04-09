package com.boeingmerryho.business.seatservice.application.dto.request;

public record SeatCreateServiceRequestDto(
	String name,
	Integer seatBlock,
	Integer seatColumn,
	Integer seatRow,
	Integer price,
	Boolean isActive
) {
}