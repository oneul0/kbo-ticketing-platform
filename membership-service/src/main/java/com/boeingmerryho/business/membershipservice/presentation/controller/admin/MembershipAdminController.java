package com.boeingmerryho.business.membershipservice.presentation.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipCreateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.service.admin.MembershipAdminService;
import com.boeingmerryho.business.membershipservice.presentation.dto.MembershipSuccessCode;
import com.boeingmerryho.business.membershipservice.presentation.dto.mapper.MembershipPresentationMapper;
import com.boeingmerryho.business.membershipservice.presentation.dto.request.MembershipCreateRequestDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipCreateResponseDto;

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

}
