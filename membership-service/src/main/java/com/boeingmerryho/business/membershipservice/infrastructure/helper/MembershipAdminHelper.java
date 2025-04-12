package com.boeingmerryho.business.membershipservice.infrastructure.helper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.membershipservice.application.dto.mapper.MembershipApplicationMapper;
import com.boeingmerryho.business.membershipservice.application.dto.query.MembershipSearchCondition;
import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipCreateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipQueryRepository;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipRepository;
import com.boeingmerryho.business.membershipservice.exception.MembershipErrorCode;
import com.boeingmerryho.business.membershipservice.presentation.dto.request.MembershipSearchAdminRequestServiceDto;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MembershipAdminHelper {

	private final MembershipApplicationMapper mapper;
	private final MembershipRepository membershipRepository;
	private final MembershipQueryRepository membershipQueryRepository;

	public Membership save(MembershipCreateRequestServiceDto requestDto) {
		Membership membership = mapper.toEntity(requestDto);
		return membershipRepository.save(membership);
	}

	public Membership getAnyMembershipById(Long id) {
		return membershipRepository.findById(id)
			.orElseThrow(() -> new GlobalException(MembershipErrorCode.NOT_FOUND));
	}

	public Page<Membership> search(MembershipSearchAdminRequestServiceDto requestServiceDto) {
		MembershipSearchCondition condition = new MembershipSearchCondition(
			requestServiceDto.season(),
			requestServiceDto.name(),
			requestServiceDto.minDiscount(),
			requestServiceDto.maxDiscount(),
			requestServiceDto.isDeleted()
		);
		return membershipQueryRepository.search(condition, requestServiceDto.customPageable());
	}
}
