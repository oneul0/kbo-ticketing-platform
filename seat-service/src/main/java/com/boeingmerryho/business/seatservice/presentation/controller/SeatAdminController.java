package com.boeingmerryho.business.seatservice.presentation.controller;

import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.seatservice.application.dto.request.SeatActiveServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatCreateServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatInActiveServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatActiveServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatCreateServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatInActiveServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.service.SeatAdminService;
import com.boeingmerryho.business.seatservice.presentation.SeatSuccessCode;
import com.boeingmerryho.business.seatservice.presentation.dto.mapper.SeatPresentationMapper;
import com.boeingmerryho.business.seatservice.presentation.dto.request.SeatCreateRequestDto;
import com.boeingmerryho.business.seatservice.presentation.dto.response.SeatCreateResponseDto;
import com.boeingmerryho.business.seatservice.presentation.dto.response.SeatResponseDto;

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

	@Description(
		"ADMIN - 좌석 이용 가능"
	)
	@PutMapping("/{id}/active")
	public SuccessResponse<SeatResponseDto> activeSeat(@PathVariable Long id) {
		SeatActiveServiceRequestDto serviceDto = seatPresentationMapper.toSeatActiveServiceRequestDto(id);

		SeatActiveServiceResponseDto seatActiveServiceResponseDto = seatAdminService.activeSeat(serviceDto);
		SeatResponseDto response = seatPresentationMapper.toSeatResponseDtoForActive(seatActiveServiceResponseDto);
		return SuccessResponse.of(SeatSuccessCode.UPDATED_SEAT, response).getBody();
	}

	@Description(
		"ADMIN - 좌석 이용 불가능"
	)
	@PutMapping("/{id}/in-active")
	public SuccessResponse<SeatResponseDto> inActiveSeat(@PathVariable Long id) {
		SeatInActiveServiceRequestDto serviceDto = seatPresentationMapper.toSeatInActiveServiceRequestDto(id);

		SeatInActiveServiceResponseDto seatInActiveServiceResponseDto = seatAdminService.inActiveSeat(serviceDto);
		SeatResponseDto response = seatPresentationMapper.toSeatResponseDtoForInActive(seatInActiveServiceResponseDto);
		return SuccessResponse.of(SeatSuccessCode.UPDATED_SEAT, response).getBody();
	}
}