package com.boeingmerryho.business.membershipservice.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.membershipservice.application.dto.query.MembershipUserSearchCondition;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUserSearchAdminResponseServiceDto;

public interface MembershipUserQueryRepository {
	Page<MembershipUserSearchAdminResponseServiceDto> searchMembershipUser(
		MembershipUserSearchCondition condition, Pageable pageable);
}