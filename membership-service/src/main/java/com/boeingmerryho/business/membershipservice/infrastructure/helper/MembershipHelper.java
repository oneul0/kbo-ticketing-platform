package com.boeingmerryho.business.membershipservice.infrastructure.helper;

import java.time.Year;
import java.util.List;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.membershipservice.application.dto.mapper.MembershipApplicationMapper;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUserDetailResponseServiceDto;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.entity.MembershipUser;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipRepository;
import com.boeingmerryho.business.membershipservice.exception.MembershipErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MembershipHelper {

	private final MembershipApplicationMapper mapper;
	private final MembershipRepository membershipRepository;

	public List<Membership> findAllBySeason() {
		Integer currentYear = Year.now().getValue();

		return membershipRepository.findAllBySeasonAndIsDeletedFalse(currentYear);
	}

	public Membership readActiveMembership(Long membershipId) {
		return membershipRepository.findByIdAndIsDeletedFalse(membershipId)
			.orElseThrow(() -> new GlobalException(MembershipErrorCode.NOT_FOUND));
	}

	public MembershipUserDetailResponseServiceDto getMembershipUser(Integer season, Long userId) {
		MembershipUser membershipUser = membershipRepository
			.findByUserIdAndMembershipSeason(userId, season)
			.orElseThrow(() -> new GlobalException(MembershipErrorCode.NOT_FOUND_MEMBERSHIP));

		Membership membership = membershipUser.getMembership();

		return new MembershipUserDetailResponseServiceDto(
			membership.getSeason(),
			membership.getName().name(),
			membership.getDiscount(),
			membershipUser.getIsActive()
		);
	}
}
