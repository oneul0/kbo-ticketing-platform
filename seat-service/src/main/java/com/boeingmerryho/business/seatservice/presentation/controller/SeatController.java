package com.boeingmerryho.business.seatservice.presentation.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.seatservice.application.dto.request.CacheBlockServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheSeatProcessServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheSeatsProcessServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.response.CacheBlockServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.service.SeatService;
import com.boeingmerryho.business.seatservice.exception.SeatSuccessCode;
import com.boeingmerryho.business.seatservice.presentation.dto.mapper.SeatPresentationMapper;
import com.boeingmerryho.business.seatservice.presentation.dto.request.CacheSeatsProcessRequestDto;
import com.boeingmerryho.business.seatservice.presentation.dto.response.CacheBlockResponseDto;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seats")
public class SeatController {
	private final SeatService seatService;
	private final SeatPresentationMapper seatPresentationMapper;

	@Description(
		"블록 별 좌석 조회하기"
	)
	@GetMapping("/blocks/{blockId}")
	public SuccessResponse<CacheBlockResponseDto> getBlockSeats(
		@PathVariable Integer blockId,
		@RequestParam("date") LocalDate date
	) {
		CacheBlockServiceRequestDto serviceDto = seatPresentationMapper.toCacheBlockServiceRequestDto(blockId, date);

		CacheBlockServiceResponseDto cacheBlockServiceResponseDto = seatService.getBlockSeats(serviceDto);
		CacheBlockResponseDto response = seatPresentationMapper.toCacheBlockResponseDto(cacheBlockServiceResponseDto);
		return SuccessResponse.of(SeatSuccessCode.OK_BLOCK, response).getBody();
	}

	@Description(
		"좌석 선점하기"
	)
	@PostMapping("/blocks/{blockId}")
	public ResponseEntity<SuccessResponse<Void>> processBlockSeats(
		@RequestAttribute Long userId,
		@PathVariable Integer blockId,
		@RequestBody CacheSeatsProcessRequestDto request
	) {
		List<CacheSeatProcessServiceRequestDto> serviceRequestSeatInfos = request.requestSeatsInfo().stream().map(
			seatPresentationMapper::toCacheSeatProcessServiceRequestDto
		).toList();

		CacheSeatsProcessServiceRequestDto serviceDto = seatPresentationMapper.toCacheSeatsProcessServiceRequestDto(
			request.matchId(),
			request.date(),
			blockId,
			serviceRequestSeatInfos
		);

		seatService.processBlockSeats(userId, serviceDto);
		return SuccessResponse.of(SeatSuccessCode.PROCESS_SEAT, null);
	}
}