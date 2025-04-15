package com.boeingmerryho.business.membershipservice.application.service.admin;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipUserSearchAdminRequestServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUserDetailAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUserSearchAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.infrastructure.helper.MembershipAdminHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembershipUserAdminService {

	private final MembershipAdminHelper membershipAdminHelper;

	@Transactional(readOnly = true)
	public MembershipUserDetailAdminResponseServiceDto getMembershipUserDetail(Long id) {
		return membershipAdminHelper.getMembershipUserDetailInfo(id);
	}

	public Page<MembershipUserSearchAdminResponseServiceDto> searchUserMembership(
		MembershipUserSearchAdminRequestServiceDto requestServiceDto) {
		return membershipAdminHelper.searchUserMembership(requestServiceDto);
	}
}
