package com.boeingmerryho.business.seatservice.application.dto.request;

public record SeatServiceUpdateDto(
	String name,
	Integer seatBlock,
	Integer seatColumn,
	Integer seatRow,
	String seatNo,
	Integer price
) {
}