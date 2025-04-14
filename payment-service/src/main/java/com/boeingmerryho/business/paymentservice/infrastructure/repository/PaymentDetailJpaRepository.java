package com.boeingmerryho.business.paymentservice.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;

public interface PaymentDetailJpaRepository extends JpaRepository<PaymentDetail, Long> {
	Optional<PaymentDetail> findPaymentDetailByPaymentId(Long id);
}
