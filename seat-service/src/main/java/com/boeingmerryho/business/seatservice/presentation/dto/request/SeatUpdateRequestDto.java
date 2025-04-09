package com.boeingmerryho.business.seatservice.presentation.dto.request;

public record SeatUpdateRequestDto(
	String name,
	Integer price
) {
}