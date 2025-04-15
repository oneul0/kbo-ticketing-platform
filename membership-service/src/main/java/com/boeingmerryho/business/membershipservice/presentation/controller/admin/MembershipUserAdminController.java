package com.boeingmerryho.business.membershipservice.presentation.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUserDetailAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUserSearchAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.service.admin.MembershipUserAdminService;
import com.boeingmerryho.business.membershipservice.presentation.dto.MembershipSuccessCode;
import com.boeingmerryho.business.membershipservice.presentation.dto.mapper.MembershipPresentationMapper;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipUserDetailAdminResponseDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipUserSearchAdminResponseDto;
import com.boeingmerryho.business.membershipservice.utils.PageableUtils;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/memberships")
public class MembershipUserAdminController {

	private final MembershipPresentationMapper mapper;
	private final MembershipUserAdminService membershipUserAdminService;

	@GetMapping("/users/{id}")
	public ResponseEntity<SuccessResponse<MembershipUserDetailAdminResponseDto>> getMembershipUserDetail(
		@PathVariable Long id
	) {
		MembershipUserDetailAdminResponseServiceDto responseServiceDto = membershipUserAdminService.getMembershipUserDetail(
			id);
		MembershipUserDetailAdminResponseDto responseDto = mapper.toMembershipUserDetailAdminResponseDto(
			responseServiceDto);
		return SuccessResponse.of(MembershipSuccessCode.FETCHED_MEMBERSHIP_USER, responseDto);
	}

	@GetMapping("/users")
	public ResponseEntity<SuccessResponse<Page<MembershipUserSearchAdminResponseDto>>> searchMembershipUser(
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
		@RequestParam(value = "by", required = false) String by,
		@RequestParam(value = "userId", required = false) Long userId,
		@RequestParam(value = "membershipId", required = false) Long membershipId,
		@RequestParam(value = "name", required = false) String name,
		@RequestParam(value = "season", required = false) Integer season,
		@RequestParam(value = "minDiscount", required = false) Double minDiscount,
		@RequestParam(value = "maxDiscount", required = false) Double maxDiscount,
		@RequestParam(value = "isDeleted", required = false) Boolean isDeleted
	) {
		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);

		Page<MembershipUserSearchAdminResponseServiceDto> responseServiceDto = membershipUserAdminService.searchUserMembership(
			mapper.toMembershipUserSearchAdminRequestServiceDto(pageable, userId, membershipId, name, season,
				minDiscount, maxDiscount,
				isDeleted));
		return SuccessResponse.of(MembershipSuccessCode.FETCHED_MEMBERSHIPS_USER,
			responseServiceDto.map(mapper::toMembershipUserSearchAdminResponseDto));
	}

	@PostMapping("/{season}/deactivate")
	public ResponseEntity<SuccessResponse<Integer>> deactivateUsersOfSeason(@PathVariable Integer season) {
		Integer count = membershipUserAdminService.deactivateUsersOfSeason(season);
		return SuccessResponse.of(MembershipSuccessCode.DEACTIVATED_MEMBERSHIP_USERS, count);
	}
}
