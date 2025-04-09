package com.boeingmerryho.business.paymentservice.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boeingmerryho.business.paymentservice.domain.entity.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
