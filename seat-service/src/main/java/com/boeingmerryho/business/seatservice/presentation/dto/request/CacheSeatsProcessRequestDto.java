package com.boeingmerryho.business.seatservice.presentation.dto.request;

import java.time.LocalDate;
import java.util.List;

public record CacheSeatsProcessRequestDto(
	Long matchId,
	LocalDate date,
	List<CacheSeatProcessRequestDto> requestSeatsInfo
) {
}