package com.boeingmerryho.business.seatservice.presentation.controller;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.seatservice.application.dto.request.CacheSeatCreateServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatActiveServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatCreateServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatDeleteServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatInActiveServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatUpdateServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatActiveServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatCreateServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatInActiveServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatUpdateServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.service.SeatAdminService;
import com.boeingmerryho.business.seatservice.exception.SeatSuccessCode;
import com.boeingmerryho.business.seatservice.presentation.dto.mapper.SeatPresentationMapper;
import com.boeingmerryho.business.seatservice.presentation.dto.request.CacheSeatCreateRequestDto;
import com.boeingmerryho.business.seatservice.presentation.dto.request.SeatCreateRequestDto;
import com.boeingmerryho.business.seatservice.presentation.dto.request.SeatUpdateRequestDto;
import com.boeingmerryho.business.seatservice.presentation.dto.response.SeatCreateResponseDto;
import com.boeingmerryho.business.seatservice.presentation.dto.response.SeatResponseDto;
import com.boeingmerryho.business.seatservice.presentation.dto.response.SeatUpdateResponseDto;

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

	@Description(
		"ADMIN - 좌석 수정하기"
	)
	@PutMapping("/{id}")
	public SuccessResponse<SeatUpdateResponseDto> updateSeat(
		@PathVariable Long id,
		@RequestBody SeatUpdateRequestDto update
	) {
		SeatUpdateServiceRequestDto serviceDto = seatPresentationMapper.toSeatUpdateServiceRequestDto(id, update);

		SeatUpdateServiceResponseDto seatUpdateServiceResponseDto = seatAdminService.updateSeat(serviceDto);
		SeatUpdateResponseDto response = seatPresentationMapper.toSeatUpdateResponseDto(seatUpdateServiceResponseDto);
		return SuccessResponse.of(SeatSuccessCode.UPDATED_SEAT, response).getBody();
	}

	@Description(
		"ADMIN - 좌석 삭제하기"
	)
	@DeleteMapping("/{id}")
	public ResponseEntity<SuccessResponse<Void>> deleteSeat(@PathVariable Long id) {
		SeatDeleteServiceRequestDto serviceDto = seatPresentationMapper.toSeatDeleteServiceRequestDto(id);

		seatAdminService.deleteSeat(serviceDto);
		return SuccessResponse.of(SeatSuccessCode.DELETED_SEAT, null);
	}

	@Description(
		"ADMIN - 입력 받은 날짜로 예약 가능 좌석 생성하기"
	)
	@PostMapping("/create/buckets")
	public ResponseEntity<SuccessResponse<Void>> createCacheSeats(@RequestBody CacheSeatCreateRequestDto create) {
		CacheSeatCreateServiceRequestDto serviceDto = seatPresentationMapper.toCacheSeatCreateServiceRequestDto(
			create.date()
		);

		seatAdminService.createCacheSeats(serviceDto);
		return SuccessResponse.of(SeatSuccessCode.CREATED_SEAT, null);
	}
}