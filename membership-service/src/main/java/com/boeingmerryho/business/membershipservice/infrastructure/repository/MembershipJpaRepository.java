package com.boeingmerryho.business.membershipservice.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipRepository;
import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;

public interface MembershipJpaRepository extends JpaRepository<Membership, Long>, MembershipRepository {

	List<Membership> findAllBySeasonAndIsDeletedFalse(Integer season);

	boolean existsBySeasonAndNameAndIsDeletedFalse(Integer season, MembershipType membershipType);

	Optional<Membership> findByIdAndIsDeletedFalse(Long id);

	@Query("""
		SELECT m FROM Membership m
		JOIN m.users mu
		WHERE mu.userId = :userId
		AND mu.season = :season
		AND mu.isActive = true
		AND mu.isDeleted = false
		""")
	Optional<Membership> findActiveMembershipByUserIdAndSeasonAndIsDeletedFalse(
		@Param("userId") Long userId,
		@Param("season") int season
	);
}
