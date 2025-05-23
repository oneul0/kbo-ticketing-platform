package com.boeingmerryho.business.membershipservice.application.service.admin;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.membershipservice.application.dto.mapper.MembershipApplicationMapper;
import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipCreateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipSearchAdminRequestServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipUpdateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipCreateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipDetailAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipSearchAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUpdateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.infrastructure.helper.MembershipAdminHelper;
import com.boeingmerryho.business.membershipservice.infrastructure.helper.MembershipRedisHelper;
import com.boeingmerryho.business.membershipservice.infrastructure.helper.MembershipValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembershipAdminService {

	private final MembershipValidator validator;
	private final MembershipApplicationMapper mapper;
	private final MembershipAdminHelper membershipAdminHelper;
	private final MembershipRedisHelper membershipRedisHelper;

	@Transactional
	public MembershipCreateResponseServiceDto createMembership(MembershipCreateRequestServiceDto requestServiceDto) {
		validator.validateNotDuplicated(requestServiceDto.season(), requestServiceDto.name());

		Membership saved = membershipAdminHelper.save(requestServiceDto);

		membershipRedisHelper.preloadStock(saved.getId(), saved.getAvailableQuantity());

		return mapper.toMembershipCreateResponseServiceDto(saved);
	}

	@Transactional(readOnly = true)
	public MembershipDetailAdminResponseServiceDto getMembershipDetail(Long id) {
		Membership membershipDetail = membershipAdminHelper.getAnyMembershipById(id);
		return mapper.toMembershipDetailAdminResponseServiceDto(membershipDetail);
	}

	@Transactional(readOnly = true)
	public Page<MembershipSearchAdminResponseServiceDto> searchMembership(
		MembershipSearchAdminRequestServiceDto requestServiceDto) {
		Page<Membership> memberships = membershipAdminHelper.search(requestServiceDto);
		return memberships.map(mapper::toMembershipSearchAdminResponseServiceDto);
	}

	@Transactional
	public MembershipUpdateResponseServiceDto updateMembership(Long id, MembershipUpdateRequestServiceDto requestDto) {
		validator.validateHasUpdatableFields(requestDto);
		Membership updated = membershipAdminHelper.updateMembershipInfo(id, requestDto);
		return mapper.toMembershipUpdateResponseServiceDto(updated);
	}

	@Transactional
	public void deleteStore(Long id) {
		membershipAdminHelper.deleteStore(id);
	}
}
