package com.boeingmerryho.business.seatservice.application.service;

import java.util.List;

import org.redisson.api.RList;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.seatservice.application.dto.mapper.SeatApplicationMapper;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheBlockServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.response.CacheBlockServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.CacheSeatServiceResponseDto;
import com.boeingmerryho.business.seatservice.domain.service.GetCacheBlockSeatsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatService {
	private final SeatApplicationMapper seatApplicationMapper;
	private final GetCacheBlockSeatsService getCacheBlockSeatsService;

	public CacheBlockServiceResponseDto getBlockSeats(CacheBlockServiceRequestDto request) {
		RList<String> blockSeats = getCacheBlockSeatsService.getBlocks(request);
		List<CacheSeatServiceResponseDto> seats = getCacheBlockSeatsService.getBlockSeats(blockSeats);

		return seatApplicationMapper.toCacheBlockServiceResponseDto(request.blockId(), seats);
	}
}