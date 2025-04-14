package com.boeingmerryho.business.membershipservice.presentation.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUserDetailAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.service.admin.MembershipAdminService;
import com.boeingmerryho.business.membershipservice.presentation.dto.MembershipSuccessCode;
import com.boeingmerryho.business.membershipservice.presentation.dto.mapper.MembershipPresentationMapper;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipUserDetailAdminResponseDto;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/memberships")
public class MembershipUserAdminController {

	private final MembershipPresentationMapper mapper;
	private final MembershipAdminService membershipAdminService;

	@GetMapping("/users/{id}")
	public ResponseEntity<SuccessResponse<MembershipUserDetailAdminResponseDto>> getMembershipUserDetail(
		@PathVariable Long id
	) {
		MembershipUserDetailAdminResponseServiceDto responseServiceDto = membershipAdminService.getMembershipUserDetail(
			id);
		MembershipUserDetailAdminResponseDto responseDto = mapper.toMembershipUserDetailAdminResponseDto(
			responseServiceDto);
		return SuccessResponse.of(MembershipSuccessCode.FETCHED_MEMBERSHIP_USER, responseDto);
	}
}
