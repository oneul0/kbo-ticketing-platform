package com.boeingmerryho.business.seatservice.presentation.dto.request;

import java.time.LocalDate;

public record CacheSeatCreateRequestDto(
	LocalDate date
) {
}