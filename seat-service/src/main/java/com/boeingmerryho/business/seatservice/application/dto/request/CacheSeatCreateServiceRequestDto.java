package com.boeingmerryho.business.seatservice.application.dto.request;

import java.time.LocalDate;

public record CacheSeatCreateServiceRequestDto(
	LocalDate date
) {
}