package com.boeingmerryho.business.paymentservice.infrastructure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boeingmerryho.business.paymentservice.domain.entity.PaymentTicket;

public interface PaymentTicketJpaRepository extends JpaRepository<PaymentTicket, Long> {
	List<PaymentTicket> findByPaymentId(Long paymentId);
}
