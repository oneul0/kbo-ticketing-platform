package com.boeingmerryho.business.membershipservice.infrastructure.helper;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.membershipservice.application.dto.mapper.MembershipApplicationMapper;
import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipCreateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipRepository;
import com.boeingmerryho.business.membershipservice.exception.MembershipErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MembershipAdminHelper {

	private final MembershipApplicationMapper mapper;
	private final MembershipRepository membershipRepository;

	public Membership save(MembershipCreateRequestServiceDto requestDto) {
		Membership membership = mapper.toEntity(requestDto);
		return membershipRepository.save(membership);
	}

	public Membership getAnyMembershipById(Long id) {
		return membershipRepository.findById(id)
			.orElseThrow(() -> new GlobalException(MembershipErrorCode.NOT_FOUND));
	}
}
