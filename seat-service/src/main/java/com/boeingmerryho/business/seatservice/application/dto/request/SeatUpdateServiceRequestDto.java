package com.boeingmerryho.business.seatservice.application.dto.request;

public record SeatUpdateServiceRequestDto(
	Long id,
	String name,
	Integer price
) {
}