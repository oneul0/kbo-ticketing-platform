package com.boeingmerryho.business.storeservice.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.storeservice.application.dto.response.StoreCreateResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.service.StoreAdminService;
import com.boeingmerryho.business.storeservice.presentation.StoreSuccessCode;
import com.boeingmerryho.business.storeservice.presentation.dto.mapper.StorePresentationMapper;
import com.boeingmerryho.business.storeservice.presentation.dto.request.StoreCreateRequestDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreCreateResponseDto;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/v1/stores")
@RequiredArgsConstructor
public class StoreAdminController {

	private final StoreAdminService storeAdminService;
	private final StorePresentationMapper mapper;

	@PostMapping
	public ResponseEntity<SuccessResponse<StoreCreateResponseDto>> createStore(
		@RequestBody @Valid StoreCreateRequestDto requestDto
	) {
		StoreCreateResponseServiceDto responseServiceDto = storeAdminService.createStore(
			mapper.toStoreCreateRequestServiceDto(requestDto)
		);
		StoreCreateResponseDto responseDto = mapper.toStoreCreateResponseDto(responseServiceDto);
		return SuccessResponse.of(StoreSuccessCode.CREATED_STORE, responseDto);
	}
}
