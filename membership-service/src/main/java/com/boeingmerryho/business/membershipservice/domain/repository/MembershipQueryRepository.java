package com.boeingmerryho.business.membershipservice.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.membershipservice.application.dto.query.MembershipSearchCondition;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;

public interface MembershipQueryRepository {
	Page<Membership> search(MembershipSearchCondition condition, Pageable pageable);
}
