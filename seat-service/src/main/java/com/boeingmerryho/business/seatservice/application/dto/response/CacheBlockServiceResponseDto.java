package com.boeingmerryho.business.seatservice.application.dto.response;

import java.util.List;

public record CacheBlockServiceResponseDto(
	Integer block,
	List<CacheSeatServiceResponseDto> seats
) {
}