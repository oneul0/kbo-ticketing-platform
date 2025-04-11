package com.boeingmerryho.business.storeservice.presentation.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreSearchResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.service.StoreService;
import com.boeingmerryho.business.storeservice.presentation.StoreSuccessCode;
import com.boeingmerryho.business.storeservice.presentation.dto.mapper.StorePresentationMapper;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreDetailResponseDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreSearchResponseDto;
import com.boeingmerryho.business.storeservice.utils.PageableUtils;

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

	@GetMapping
	public ResponseEntity<SuccessResponse<Page<StoreSearchResponseDto>>> searchStore(
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
		@RequestParam(value = "by", required = false) String by,
		@RequestParam(value = "stadiumId", required = false) Long stadiumId,
		@RequestParam(value = "name", required = false) String name,
		@RequestParam(value = "isClosed", required = false) Boolean isClosed
	) {
		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);

		Page<StoreSearchResponseServiceDto> responseServiceDto = storeService.searchStore(
			mapper.toStoreSearchRequestServiceDto(pageable, stadiumId, name, isClosed));
		return SuccessResponse.of(StoreSuccessCode.FETCHED_STORES,
			responseServiceDto.map(mapper::toStoreSearchResponseDto));
	}
}
