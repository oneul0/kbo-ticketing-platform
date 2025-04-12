package com.boeingmerryho.business.seatservice.presentation.controller;

import java.time.LocalDate;

import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.seatservice.application.dto.request.CacheBlockServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.response.CacheBlockServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.service.SeatService;
import com.boeingmerryho.business.seatservice.presentation.SeatSuccessCode;
import com.boeingmerryho.business.seatservice.presentation.dto.mapper.SeatPresentationMapper;
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
}