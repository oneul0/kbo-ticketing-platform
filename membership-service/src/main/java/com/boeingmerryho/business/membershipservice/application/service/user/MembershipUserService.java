package com.boeingmerryho.business.membershipservice.application.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUserDetailResponseServiceDto;
import com.boeingmerryho.business.membershipservice.infrastructure.helper.MembershipHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembershipUserService {

	private final MembershipHelper membershipHelper;

	@Transactional(readOnly = true)
	public MembershipUserDetailResponseServiceDto getMembershipUser(Integer season, Long userId) {
		return membershipHelper.getMembershipUser(season, userId);
	}
}
