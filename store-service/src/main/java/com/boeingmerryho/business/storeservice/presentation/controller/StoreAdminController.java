package com.boeingmerryho.business.storeservice.presentation.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.storeservice.application.dto.response.StoreCreateResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailAdminResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreSearchAdminResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.service.StoreAdminService;
import com.boeingmerryho.business.storeservice.presentation.StoreSuccessCode;
import com.boeingmerryho.business.storeservice.presentation.dto.mapper.StorePresentationMapper;
import com.boeingmerryho.business.storeservice.presentation.dto.request.StoreCreateRequestDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreCreateResponseDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreDetailAdminResponseDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreSearchAdminResponseDto;
import com.boeingmerryho.business.storeservice.utils.PageableUtils;

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

	@GetMapping("/{id}")
	public ResponseEntity<SuccessResponse<StoreDetailAdminResponseDto>> getStoreDetail(
		@PathVariable Long id
	) {
		StoreDetailAdminResponseServiceDto responseServiceDto = storeAdminService.getStoreDetail(id);
		StoreDetailAdminResponseDto responseDto = mapper.toStoreDetailAdminResponseDto(responseServiceDto);
		return SuccessResponse.of(StoreSuccessCode.FETCHED_STORE, responseDto);
	}

	@GetMapping
	public ResponseEntity<SuccessResponse<Page<StoreSearchAdminResponseDto>>> searchStore(
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
		@RequestParam(value = "by", required = false) String by,
		@RequestParam(value = "stadiumId", required = false) Long stadiumId,
		@RequestParam(value = "name", required = false) String name,
		@RequestParam(value = "isClosed", required = false) Boolean isClosed,
		@RequestParam(value = "isDeleted", required = false) Boolean isDeleted
	) {
		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);

		Page<StoreSearchAdminResponseServiceDto> responseServiceDto = storeAdminService.searchStore(
			mapper.toStoreSearchAdminRequestServiceDto(pageable, stadiumId, name, isClosed, isDeleted));
		return SuccessResponse.of(StoreSuccessCode.FETCHED_STORES,
			responseServiceDto.map(mapper::toStoreSearchAdminResponseDto));
	}
}
