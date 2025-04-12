package com.boeingmerryho.business.membershipservice.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipRepository;
import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;

public interface MembershipJpaRepository extends JpaRepository<Membership, Long>, MembershipRepository {

	boolean existsBySeasonAndName(Integer season, MembershipType membershipType);

}
