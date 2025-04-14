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

	@Query("SELECT M " +
		"FROM Membership M " +
		"JOIN M.users MU " +
		"WHERE MU.userId = :userId " +
		"AND MU.season = :season " +
		"AND MU.isActive IS TRUE " +
		"AND MU.isDeleted IS FALSE")
	Optional<Membership> findActiveMembershipByUserIdAndSeasonAndIsDeletedFalse(
		@Param("userId") Long userId,
		@Param("season") int season
	);

	@Query("SELECT M " +
		"FROM Membership M " +
		"JOIN M.users MU " +
		"WHERE MU.id = :membershipUserId ")
	Optional<Membership> findMembershipByMembershipUserId(@Param("membershipUserId") Long membershipUserId);
}
