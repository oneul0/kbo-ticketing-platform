package com.boeingmerryho.business.membershipservice.infrastructure.helper;

import java.time.Year;
import java.util.List;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.membershipservice.application.dto.mapper.MembershipApplicationMapper;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MembershipHelper {

	private final MembershipApplicationMapper mapper;
	private final MembershipRepository membershipRepository;

	public List<Membership> findAllBySeason() {
		Integer currentYear = Year.now().getValue();

		return membershipRepository.findAllBySeason(currentYear);
	}
}
