package com.boeingmerryho.business.membershipservice.presentation.controller.admin;

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

import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipCreateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipDetailAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipSearchAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUpdateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.service.admin.MembershipAdminService;
import com.boeingmerryho.business.membershipservice.presentation.dto.MembershipSuccessCode;
import com.boeingmerryho.business.membershipservice.presentation.dto.mapper.MembershipPresentationMapper;
import com.boeingmerryho.business.membershipservice.presentation.dto.request.MembershipCreateRequestDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.request.MembershipUpdateRequestDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipCreateResponseDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipDetailAdminResponseDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipSearchAdminResponseDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipUpdateResponseDto;
import com.boeingmerryho.business.membershipservice.utils.PageableUtils;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/memberships")
public class MembershipAdminController {

	private final MembershipAdminService membershipAdminService;
	private final MembershipPresentationMapper mapper;

	@PostMapping
	public ResponseEntity<SuccessResponse<MembershipCreateResponseDto>> createMembership(
		@RequestBody @Valid MembershipCreateRequestDto requestDto
	) {
		MembershipCreateResponseServiceDto responseServiceDto = membershipAdminService.createMembership(
			mapper.toMembershipCreateRequestServiceDto(requestDto)
		);
		MembershipCreateResponseDto responseDto = mapper.toMembershipCreateResponseDto(responseServiceDto);
		return SuccessResponse.of(MembershipSuccessCode.CREATED_MEMBERSHIP, responseDto);
	}

	@GetMapping("/{id}")
	public ResponseEntity<SuccessResponse<MembershipDetailAdminResponseDto>> getMembershipDetail(
		@PathVariable Long id
	) {
		MembershipDetailAdminResponseServiceDto responseServiceDto = membershipAdminService.getMembershipDetail(id);
		MembershipDetailAdminResponseDto responseDto = mapper.toMembershipDetailAdminResponseDto(responseServiceDto);
		return SuccessResponse.of(MembershipSuccessCode.FETCHED_MEMBERSHIP, responseDto);
	}

	@GetMapping
	public ResponseEntity<SuccessResponse<Page<MembershipSearchAdminResponseDto>>> searchMembership(
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
		@RequestParam(value = "by", required = false) String by,
		@RequestParam(value = "name", required = false) String name,
		@RequestParam(value = "season", required = false) Integer season,
		@RequestParam(value = "minDiscount", required = false) Double minDiscount,
		@RequestParam(value = "maxDiscount", required = false) Double maxDiscount,
		@RequestParam(value = "minAvailableQuantity", required = false) Integer minAvailableQuantity,
		@RequestParam(value = "maxAvailableQuantity", required = false) Integer maxAvailableQuantity,
		@RequestParam(value = "minPrice", required = false) Integer minPrice,
		@RequestParam(value = "maxPrice", required = false) Integer maxPrice
	) {
		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);

		Page<MembershipSearchAdminResponseServiceDto> responseServiceDto = membershipAdminService.searchMembership(
			mapper.toMembershipSearchAdminRequestServiceDto(pageable, name, season, minDiscount, maxDiscount,
				minAvailableQuantity, maxAvailableQuantity, minPrice, maxPrice));
		return SuccessResponse.of(MembershipSuccessCode.FETCHED_MEMBERSHIPS,
			responseServiceDto.map(mapper::toMembershipSearchAdminResponseDto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<SuccessResponse<MembershipUpdateResponseDto>> updateMembership(
		@PathVariable Long id,
		@RequestBody MembershipUpdateRequestDto requestDto
	) {
		MembershipUpdateResponseServiceDto responseServiceDto = membershipAdminService.updateMembership(
			id,
			mapper.toMembershipUpdateRequestServiceDto(requestDto));
		MembershipUpdateResponseDto responseDto = mapper.toMembershipUpdateResponseDto(responseServiceDto);
		return SuccessResponse.of(MembershipSuccessCode.UPDATED_STORE, responseDto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<SuccessResponse<Void>> deleteMembership(
		@PathVariable Long id
	) {
		membershipAdminService.deleteStore(id);
		return SuccessResponse.of(MembershipSuccessCode.DELETE_MEMBERSHIP);
	}
}
