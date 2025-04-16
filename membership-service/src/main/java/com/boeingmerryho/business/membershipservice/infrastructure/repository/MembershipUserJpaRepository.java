package com.boeingmerryho.business.membershipservice.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipUserRepository;

public interface MembershipUserJpaRepository extends JpaRepository<Membership, Long>, MembershipUserRepository {
}
