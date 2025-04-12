package com.boeingmerryho.business.membershipservice.domain.repository;

import java.time.Year;
import java.util.Optional;

import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;

public interface MembershipRepository {
	Membership save(Membership membership);

	boolean existsBySeasonAndName(Year season, MembershipType membershipType);

	Optional<Membership> findById(Long id);
}
