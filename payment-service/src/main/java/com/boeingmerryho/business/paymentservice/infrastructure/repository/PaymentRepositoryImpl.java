package com.boeingmerryho.business.paymentservice.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
	private final PaymentJpaRepository paymentJpaRepository;
	private final PaymentQueryRepository paymentQueryRepository;

	@Override
	public Payment save(Payment payment) {
		return paymentJpaRepository.save(payment);
	}

	@Override
	public Optional<PaymentDetail> findPaymentDetailById(Long id) {
		return paymentQueryRepository.findPaymentDetailById(id);
	}

}
