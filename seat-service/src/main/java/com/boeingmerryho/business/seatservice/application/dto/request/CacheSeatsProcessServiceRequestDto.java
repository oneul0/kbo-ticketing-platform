package com.boeingmerryho.business.seatservice.application.dto.request;

import java.time.LocalDate;
import java.util.List;

public record CacheSeatsProcessServiceRequestDto(
	Long matchId,
	LocalDate date,
	Integer blockId,
	List<CacheSeatProcessServiceRequestDto> serviceRequestSeatInfos
) {
}