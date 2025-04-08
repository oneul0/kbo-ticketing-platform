package com.boeingmerryho.business.seatservice.presentation.controller;

import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.seatservice.application.dto.request.SeatCreateServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatCreateServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.service.SeatAdminService;
import com.boeingmerryho.business.seatservice.presentation.SeatSuccessCode;
import com.boeingmerryho.business.seatservice.presentation.dto.mapper.SeatPresentationMapper;
import com.boeingmerryho.business.seatservice.presentation.dto.request.SeatCreateRequestDto;
import com.boeingmerryho.business.seatservice.presentation.dto.response.SeatCreateResponseDto;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/seats")
public class SeatAdminController {
	private final SeatAdminService seatAdminService;
	private final SeatPresentationMapper seatPresentationMapper;

	@Description(
		"ADMIN - 좌석 추가하기"
	)
	@PostMapping
	public SuccessResponse<SeatCreateResponseDto> createSeat(@RequestBody SeatCreateRequestDto create) {
		SeatCreateServiceRequestDto serviceCreate = seatPresentationMapper.toSeatCreateServiceRequestDto(
			create
		);

		SeatCreateServiceResponseDto seatCreateServiceResponseDto = seatAdminService.createSeat(serviceCreate);
		SeatCreateResponseDto response = seatPresentationMapper.toSeatCreateResponseDto(
			seatCreateServiceResponseDto
		);
		return SuccessResponse.of(SeatSuccessCode.CREATED_SEAT, response).getBody();
	}
}