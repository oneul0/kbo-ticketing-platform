package com.boeingmerryho.business.storeservice.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.service.StoreService;
import com.boeingmerryho.business.storeservice.presentation.StoreSuccessCode;
import com.boeingmerryho.business.storeservice.presentation.dto.mapper.StorePresentationMapper;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreDetailResponseDto;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;
	private final StorePresentationMapper mapper;

	@GetMapping("/{id}")
	public ResponseEntity<SuccessResponse<StoreDetailResponseDto>> getStoreDetail(
		@PathVariable Long id
	) {
		StoreDetailResponseServiceDto responseServiceDto = storeService.getStoreDetail(id);
		StoreDetailResponseDto responseDto = mapper.toStoreDetailResponseDto(responseServiceDto);
		return SuccessResponse.of(StoreSuccessCode.FETCHED_STORE, responseDto);
	}
}
