package com.boeingmerryho.business.seatservice.presentation.dto.response;

import java.util.List;

public record CacheBlockResponseDto(
	Integer block,
	List<CacheSeatResponseDto> seats
) {
}