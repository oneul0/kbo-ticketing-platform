package com.boeingmerryho.business.paymentservice.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boeingmerryho.business.paymentservice.domain.entity.PaymentMembership;

public interface PaymentMembershipJpaRepository extends JpaRepository<PaymentMembership, Long> {
	Optional<PaymentMembership> findByPaymentId(Long paymentId);
}
