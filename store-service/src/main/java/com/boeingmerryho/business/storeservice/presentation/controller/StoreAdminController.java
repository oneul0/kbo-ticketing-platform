package com.boeingmerryho.business.storeservice.presentation.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.storeservice.application.dto.response.StoreCreateResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailAdminResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreSearchAdminResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreUpdateResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.service.StoreAdminService;
import com.boeingmerryho.business.storeservice.presentation.StoreSuccessCode;
import com.boeingmerryho.business.storeservice.presentation.dto.mapper.StorePresentationMapper;
import com.boeingmerryho.business.storeservice.presentation.dto.request.StoreCreateRequestDto;
import com.boeingmerryho.business.storeservice.presentation.dto.request.StoreUpdateRequestDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreCreateResponseDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreDetailAdminResponseDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreSearchAdminResponseDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreUpdateResponseDto;
import com.boeingmerryho.business.storeservice.utils.PageableUtils;

import io.github.boeingmerryho.commonlibrary.entity.UserRoleType;
import io.github.boeingmerryho.commonlibrary.interceptor.RequiredRoles;
import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/v1/stores")
@RequiredArgsConstructor
public class StoreAdminController {

	private final StorePresentationMapper mapper;
	private final StoreAdminService storeAdminService;

	@PostMapping
	@RequiredRoles({UserRoleType.ADMIN, UserRoleType.MANAGER})
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
	@RequiredRoles({UserRoleType.ADMIN, UserRoleType.MANAGER})
	public ResponseEntity<SuccessResponse<StoreDetailAdminResponseDto>> getStoreDetail(
		@PathVariable Long id
	) {
		StoreDetailAdminResponseServiceDto responseServiceDto = storeAdminService.getStoreDetail(id);
		StoreDetailAdminResponseDto responseDto = mapper.toStoreDetailAdminResponseDto(responseServiceDto);
		return SuccessResponse.of(StoreSuccessCode.FETCHED_STORE, responseDto);
	}

	@GetMapping
	@RequiredRoles({UserRoleType.ADMIN, UserRoleType.MANAGER})
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

	@PutMapping("/{id}")
	@RequiredRoles({UserRoleType.ADMIN, UserRoleType.MANAGER})
	public ResponseEntity<SuccessResponse<StoreUpdateResponseDto>> updateStore(
		@PathVariable Long id,
		@RequestBody StoreUpdateRequestDto requestDto
	) {
		StoreUpdateResponseServiceDto responseServiceDto = storeAdminService.updateStore(
			id,
			mapper.toStoreUpdateRequestServiceDto(requestDto));
		StoreUpdateResponseDto responseDto = mapper.toStoreUpdateResponseDto(responseServiceDto);
		return SuccessResponse.of(StoreSuccessCode.UPDATED_STORE, responseDto);
	}

	@PutMapping("/{id}/open")
	@RequiredRoles({UserRoleType.ADMIN, UserRoleType.MANAGER})
	public ResponseEntity<SuccessResponse<StoreUpdateResponseDto>> openStore(
		@PathVariable Long id
	) {
		StoreUpdateResponseServiceDto responseDtoService = storeAdminService.openStore(id);
		StoreUpdateResponseDto responseDto = mapper.toStoreUpdateResponseDto(responseDtoService);
		return SuccessResponse.of(StoreSuccessCode.OPEN_STORE, responseDto);
	}

	@PutMapping("/{id}/close")
	@RequiredRoles({UserRoleType.ADMIN, UserRoleType.MANAGER})
	public ResponseEntity<SuccessResponse<StoreUpdateResponseDto>> closeStore(
		@PathVariable Long id
	) {
		StoreUpdateResponseServiceDto responseDtoService = storeAdminService.closeStore(id);
		StoreUpdateResponseDto responseDto = mapper.toStoreUpdateResponseDto(responseDtoService);
		return SuccessResponse.of(StoreSuccessCode.CLOSE_STORE, responseDto);
	}

	@PutMapping("/{id}/queue/enable")
	@RequiredRoles({UserRoleType.ADMIN, UserRoleType.MANAGER})
	public ResponseEntity<SuccessResponse<Void>> enableQueue(@PathVariable Long id) {
		storeAdminService.enableQueue(id);
		return SuccessResponse.of(StoreSuccessCode.QUEUE_ENABLED);
	}

	@PutMapping("/{id}/queue/disable")
	@RequiredRoles({UserRoleType.ADMIN, UserRoleType.MANAGER})
	public ResponseEntity<SuccessResponse<Void>> disableQueue(@PathVariable Long id) {
		storeAdminService.disableQueue(id);
		return SuccessResponse.of(StoreSuccessCode.QUEUE_DISABLED);
	}

	@DeleteMapping("/{id}")
	@RequiredRoles({UserRoleType.ADMIN, UserRoleType.MANAGER})
	public ResponseEntity<SuccessResponse<Void>> deleteStore(@PathVariable Long id) {
		storeAdminService.deleteStore(id);
		return SuccessResponse.of(StoreSuccessCode.DELETE_STORE);
	}

}
