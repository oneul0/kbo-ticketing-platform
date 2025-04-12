package com.boeingmerryho.business.membershipservice.application.service.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.membershipservice.application.dto.mapper.MembershipApplicationMapper;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipDetailResponseServiceDto;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.infrastructure.helper.MembershipHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembershipService {

	private final MembershipHelper membershipHelper;
	private final MembershipApplicationMapper mapper;

	@Transactional(readOnly = true)
	public List<MembershipDetailResponseServiceDto> getMembershipsByCurrentSeason() {
		List<Membership> memberships = membershipHelper.findAllBySeason();

		return memberships.stream()
			.map(mapper::toMembershipDetailResponseServiceDto)
			.toList();
	}
}
