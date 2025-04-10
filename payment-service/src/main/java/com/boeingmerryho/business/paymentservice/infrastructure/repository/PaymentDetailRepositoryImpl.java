package com.boeingmerryho.business.paymentservice.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentDetailRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentDetailRepositoryImpl implements PaymentDetailRepository {
	private final PaymentDetailJpaRepository paymentDetailJpaRepository;

	@Override
	public PaymentDetail save(PaymentDetail paymentDetail) {
		return paymentDetailJpaRepository.save(paymentDetail);
	}

	@Override
	public Optional<PaymentDetail> findById(Long id) {
		return paymentDetailJpaRepository.findById(id);
	}
}
