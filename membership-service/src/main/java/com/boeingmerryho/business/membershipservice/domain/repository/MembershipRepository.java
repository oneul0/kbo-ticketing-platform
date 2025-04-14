package com.boeingmerryho.business.membershipservice.domain.repository;

import java.util.List;
import java.util.Optional;

import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;

public interface MembershipRepository {
	Membership save(Membership membership);

	boolean existsBySeasonAndName(Integer season, MembershipType membershipType);

	Optional<Membership> findById(Long id);

	List<Membership> findAllBySeason(Integer currentYear);

	Optional<Membership> findByIdAndIsDeletedFalse(Long id);

	Optional<Membership> findActiveMembershipByUserIdAndSeason(Long userId, int season);
}
