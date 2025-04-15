package com.boeingmerryho.business.membershipservice.presentation.controller.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipDetailResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUserCreateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.service.user.MembershipReserveService;
import com.boeingmerryho.business.membershipservice.application.service.user.MembershipService;
import com.boeingmerryho.business.membershipservice.presentation.dto.MembershipSuccessCode;
import com.boeingmerryho.business.membershipservice.presentation.dto.mapper.MembershipPresentationMapper;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipDetailResponseDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipUserCreateResponseDto;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/memberships")
public class MembershipController {

	private final MembershipPresentationMapper mapper;
	private final MembershipService membershipService;
	private final MembershipReserveService membershipReserveService;

	@GetMapping("/season")
	public ResponseEntity<SuccessResponse<List<MembershipDetailResponseDto>>> getMembershipsByCurrentSeason() {
		List<MembershipDetailResponseServiceDto> responseServiceDto = membershipService.getMembershipsByCurrentSeason();
		List<MembershipDetailResponseDto> responseDto = mapper.toMembershipDetailResponseDto(responseServiceDto);
		return SuccessResponse.of(MembershipSuccessCode.FETCHED_MEMBERSHIP, responseDto);
	}

	@PostMapping("/{membershipId}/reserve")
	public ResponseEntity<SuccessResponse<MembershipUserCreateResponseDto>> reserveMembership(
		@PathVariable Long membershipId
	) {
		// TODO: userId 받아오기
		Long userId = 6L;
		MembershipUserCreateResponseServiceDto responseServiceDto = membershipReserveService.reserveMembership(
			mapper.toMembershipUserCreateRequestServiceDto(membershipId, userId));
		MembershipUserCreateResponseDto responseDto = mapper.toMembershipUserCreateResponseDto(responseServiceDto);
		return SuccessResponse.of(MembershipSuccessCode.CREATED_MEMBERSHIP_USER, responseDto);
	}
}
