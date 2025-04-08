package com.boeingmerryho.business.paymentservice.domain.repository;

import java.util.Optional;

import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;

public interface PaymentRepository {
	Payment save(Payment payment);

	Optional<PaymentDetail> findPaymentDetailById(Long id);
}
