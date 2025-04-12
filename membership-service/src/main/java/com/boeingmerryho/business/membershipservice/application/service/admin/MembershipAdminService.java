package com.boeingmerryho.business.membershipservice.application.service.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.membershipservice.application.dto.mapper.MembershipApplicationMapper;
import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipCreateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipCreateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipDetailAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.infrastructure.helper.MembershipAdminHelper;
import com.boeingmerryho.business.membershipservice.infrastructure.helper.MembershipValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembershipAdminService {

	private final MembershipValidator validator;
	private final MembershipApplicationMapper mapper;
	private final MembershipAdminHelper membershipAdminHelper;

	@Transactional
	public MembershipCreateResponseServiceDto createMembership(MembershipCreateRequestServiceDto requestServiceDto) {
		validator.validateNotDuplicated(requestServiceDto.season(), requestServiceDto.name());

		Membership saved = membershipAdminHelper.save(requestServiceDto);
		return mapper.toMembershipCreateResponseServiceDto(saved);
	}

	@Transactional(readOnly = true)
	public MembershipDetailAdminResponseServiceDto getMembershipDetail(Long id) {
		Membership membershipDetail = membershipAdminHelper.getAnyMembershipById(id);

		return mapper.toMembershipDetailAdminResponseServiceDto(membershipDetail);

	}
}
