package com.boeingmerryho.business.membershipservice.domain.repository;

import com.boeingmerryho.business.membershipservice.domain.entity.MembershipUser;

public interface MembershipUserRepository {
	MembershipUser save(MembershipUser membershipUser);
}
