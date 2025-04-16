package com.boeingmerryho.business.membershipservice.presentation.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUserDetailResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.service.user.MembershipUserService;
import com.boeingmerryho.business.membershipservice.presentation.dto.MembershipSuccessCode;
import com.boeingmerryho.business.membershipservice.presentation.dto.mapper.MembershipPresentationMapper;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipUserDetailResponseDto;

import io.github.boeingmerryho.commonlibrary.entity.UserRoleType;
import io.github.boeingmerryho.commonlibrary.interceptor.RequiredRoles;
import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/memberships")
public class MembershipUserController {

	private final MembershipPresentationMapper mapper;
	private final MembershipUserService membershipUserService;

	@GetMapping("/users/season/{season}")
	@RequiredRoles({UserRoleType.NORMAL, UserRoleType.SENIOR})
	public ResponseEntity<SuccessResponse<MembershipUserDetailResponseDto>> getMembershipUserDetail(
		@PathVariable Integer season,
		@RequestAttribute Long userId
	) {
		MembershipUserDetailResponseServiceDto responseServiceDto = membershipUserService.getMembershipUser(
			season, userId);
		MembershipUserDetailResponseDto responseDto = mapper.toMembershipUserDetailResponseDto(
			responseServiceDto);
		return SuccessResponse.of(MembershipSuccessCode.FETCHED_MEMBERSHIP_USER, responseDto);
	}
}
