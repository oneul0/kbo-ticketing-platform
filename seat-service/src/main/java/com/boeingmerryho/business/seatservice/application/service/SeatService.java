package com.boeingmerryho.business.seatservice.application.service;

import java.util.ArrayList;
import java.util.List;

import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.seatservice.application.dto.mapper.SeatApplicationMapper;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheBlockServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheSeatsProcessServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.ToTicketMatchDto;
import com.boeingmerryho.business.seatservice.application.dto.request.ToTicketSeatDto;
import com.boeingmerryho.business.seatservice.application.dto.response.CacheBlockServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.CacheSeatServiceResponseDto;
import com.boeingmerryho.business.seatservice.domain.service.GetCacheBlockSeatsService;
import com.boeingmerryho.business.seatservice.domain.service.ProcessBlockSeatsService;
import com.boeingmerryho.business.seatservice.exception.SeatErrorCode;
import com.boeingmerryho.business.seatservice.infrastructure.helper.SeatCommonHelper;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatService {
	private final SeatCommonHelper seatCommonHelper;
	private final SeatApplicationMapper seatApplicationMapper;
	private final ProcessBlockSeatsService processBlockSeatsService;
	private final GetCacheBlockSeatsService getCacheBlockSeatsService;

	@Transactional(readOnly = true)
	public CacheBlockServiceResponseDto getBlockSeats(CacheBlockServiceRequestDto request) {
		RList<String> blockSeats = getCacheBlockSeatsService.getBlocks(request);
		List<CacheSeatServiceResponseDto> seats = getCacheBlockSeatsService.getBlockSeats(blockSeats);

		return seatApplicationMapper.toCacheBlockServiceResponseDto(request.blockId(), seats);
	}

	@Transactional
	public void processBlockSeats(Long userId, CacheSeatsProcessServiceRequestDto serviceDto) {
		if (serviceDto.serviceRequestSeatInfos().size() > 4) {
			throw new GlobalException(SeatErrorCode.NOT_EXCEED_4_SEAT);
		}

		ToTicketMatchDto matchInfo = processBlockSeatsService.getMatchInfo(serviceDto);

		String cacheBlockKey = seatCommonHelper.createCacheBlockKey(serviceDto.blockId(), serviceDto.date());
		RList<String> blockSeats = processBlockSeatsService.getBlockSeats(cacheBlockKey);

		List<RLock> locks = new ArrayList<>();
		List<String> requestSeats = new ArrayList<>();
		List<ToTicketSeatDto> seatInfos = new ArrayList<>();

		processBlockSeatsService.getBlockSeats(
			locks,
			cacheBlockKey,
			blockSeats,
			requestSeats,
			serviceDto.serviceRequestSeatInfos()
		);
		processBlockSeatsService.processBlockSeats(userId, matchInfo, locks, requestSeats, seatInfos);
	}
}