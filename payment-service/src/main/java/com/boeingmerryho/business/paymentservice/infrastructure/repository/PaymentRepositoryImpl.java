package com.boeingmerryho.business.paymentservice.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.paymentservice.domain.context.PaymentDetailSearchContext;
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
	public Optional<PaymentDetail> findPaymentDetailByIdAndIsDeleted(Long id) {
		return paymentQueryRepository.findPaymentDetailByIdAndIsDeletedFalse(id);
	}

	@Override
	public Page<PaymentDetail> searchPaymentDetail(PaymentDetailSearchContext context) {
		return paymentQueryRepository.searchPaymentDetail(context);
	}

	@Override
	public Optional<PaymentDetail> findPaymentDetailById(Long id) {
		return paymentQueryRepository.findPaymentDetailById(id);
	}

}
