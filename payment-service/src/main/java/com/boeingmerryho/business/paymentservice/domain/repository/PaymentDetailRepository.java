package com.boeingmerryho.business.paymentservice.domain.repository;

import java.util.Optional;

import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;

public interface PaymentDetailRepository {
	PaymentDetail save(PaymentDetail paymentDetail);

	Optional<PaymentDetail> findById(Long id);
}
