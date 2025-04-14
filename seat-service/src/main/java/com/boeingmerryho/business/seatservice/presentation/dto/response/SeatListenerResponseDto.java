package com.boeingmerryho.business.seatservice.presentation.dto.response;

import java.util.List;

public record SeatListenerResponseDto(
	String matchDate,
	List<String> seatIds
) {
}