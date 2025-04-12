package com.boeingmerryho.business.membershipservice.domain.repository;

import java.time.Year;

import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;

public interface MembershipRepository {
	Membership save(Membership membership);

	boolean existsBySeasonAndName(Year season, MembershipType membershipType);
}
