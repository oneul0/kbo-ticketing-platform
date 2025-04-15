package com.boeingmerryho.business.membershipservice.domain.repository;

import java.util.List;
import java.util.Optional;

import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.entity.MembershipUser;
import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;

public interface MembershipRepository {
	Optional<Membership> findById(Long id);

	Membership save(Membership membership);

	List<Membership> findAllBySeasonAndIsDeletedFalse(Integer currentYear);

	Optional<Membership> findByIdAndIsDeletedFalse(Long id);

	boolean existsBySeasonAndNameAndIsDeletedFalse(Integer season, MembershipType membershipType);

	Optional<Membership> findActiveMembershipByUserIdAndSeasonAndIsDeletedFalse(Long userId, int season);

	List<Membership> findAll();

	Optional<Membership> findMembershipUserWithMembership(Long membershipUserId);

	Optional<MembershipUser> findByUserIdAndMembershipSeason(Long userId, Integer season);

	Integer bulkDeactivateBySeason(Integer season);
}
