package com.boeingmerryho.business.membershipservice.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipRepository;

public interface MembershipJpaRepository extends JpaRepository<Membership, Long>, MembershipRepository {
}
